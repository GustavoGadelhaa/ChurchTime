# Mensageria (RabbitMQ) — contrato para consumidores

Este documento descreve tudo que um **listener/consumidor externo** precisa saber para integrar com o backend ChurchTime. O código neste repositório **apenas publica** mensagens; **não há consumidor** implementado aqui (o destino é outro microserviço, por exemplo de notificações).

## Visão geral

| Item | Valor |
|------|--------|
| Broker | **RabbitMQ** (AMQP 0-9-1) |
| Biblioteca no backend | Spring AMQP (`spring-boot-starter-amqp`) |
| Fila lógica | `fila.eventos` |
| Formato do corpo | **JSON** (`application/json`), via `Jackson2JsonMessageConverter` |

O backend declara a fila como **durável** (`durable = true`). Na publicação é usado o **exchange padrão** (direct implícito do RabbitMQ) com **routing key** igual ao nome da fila (`fila.eventos`), o que entrega a mensagem nessa fila.

Referências no código:

- Fila e beans: `com.church.backend.config.rabbitMQ.RabbitConfig`
- Publicação: `com.church.backend.config.messaging.NotificationProducer`
- Payload: `com.church.backend.config.messaging.dtoMessage.EventMessageDTO`

## Conexão (desenvolvimento local)

O `docker-compose.yml` na pasta `UNIFOR` sobe o RabbitMQ com interface de gestão:

- **AMQP:** porta `5672`
- **Management UI:** porta `15672`
- **Usuário / senha padrão (compose):** `guest` / `guest`

O `application.yml` **não** define `spring.rabbitmq.*`; em ambiente local o Spring Boot usa o padrão **localhost:5672** e credenciais **guest/guest** (válidas apenas em conexão local ao broker).

Para produção, configure variáveis ou perfil Spring, por exemplo:

- `spring.rabbitmq.host`, `port`, `username`, `password`, `virtual-host`, TLS, etc.

## Quando as mensagens são enviadas

1. Um job agendado (`DailyJob`) executa periodicamente (intervalo configurado em `@Scheduled` — ver código atual em `DailyJob`).
2. É executada a consulta `EventRepository.findUpcomingWithinSixHours()`, que seleciona eventos onde:
   - `reminded = false`
   - `event_date` está **no futuro** e **dentro da próxima janela de 6 horas** (em relação ao instante atual no banco, `NOW()`).
3. Para esses eventos, o backend **marca `reminded = true`** no banco e, em seguida, **envia uma mensagem por evento** para `fila.eventos`.

Implicações para o consumidor:

- Cada mensagem corresponde a **um evento** que acabou de ser marcado como “já lembrado”. Não há reenvio automático pelo mesmo fluxo para o mesmo evento (a menos que dados sejam resetados manualmente).
- Se o consumidor falhar após o backend já ter marcado `reminded`, **essa notificação não será reemitida** por este job. Trate idempotência, DLQ ou reconciliação conforme a necessidade do negócio.

## Contrato do payload (`EventMessageDTO`)

O corpo JSON reflete a classe `EventMessageDTO`:

| Campo | Tipo | Origem |
|-------|------|--------|
| `id` | número inteiro (long) | ID do evento (`events.id`) |
| `title` | string | Título do evento |
| `location` | string | Local |
| `event_date` | data/hora | Data/hora do evento (`events.event_date`) |
| `group_id` | número inteiro (long) | ID do grupo (`events.group_id`) |

A consulta nativa que preenche o DTO está em `EventRepository.findUpcomingWithinSixHours()`.

**Nomes JSON:** os campos seguem o padrão de propriedades JavaBean expostas ao Jackson (incluindo `event_date` e `group_id` com sublinhado). Consumidores em outras linguagens devem mapear esses nomes.

**Tipo de `event_date`:** no Java é `java.sql.Timestamp`; em JSON o Spring/Jackson costuma serializar como **string ISO-8601** ou formato numérico conforme configuração global do `ObjectMapper`. Se precisar de formato fixo, valide com uma mensagem real capturada na fila ou no Management UI.

### Cabeçalhos AMQP (Spring)

O `Jackson2JsonMessageConverter` do Spring AMQP costuma incluir metadados para desserialização em Java, em especial:

- `contentType`: tipicamente `application/json`
- `__TypeId__` (ou equivalente na versão em uso): frequentemente o nome da classe Java de origem, por exemplo `com.church.backend.config.messaging.dtoMessage.EventMessageDTO`

Consumidores **não Java** podem **ignorar** `__TypeId__` e interpretar só o JSON do corpo, desde que o schema acima seja respeitado.

## Fila e encadeamento AMQP

- **Nome da fila:** `fila.eventos`
- **Durabilidade:** fila durável (sobrevive a restart do broker se persistência estiver correta).
- **Exchange de publicação (lado produtor):** default exchange; **routing key** = `fila.eventos`.

Para **assinar** a mesma fila em outro serviço:

1. Conectar ao mesmo broker (ou vhost) com credenciais adequadas.
2. Declarar a fila com o **mesmo nome** `fila.eventos` (e mesma durabilidade, se quiser alinhar ao produtor) **ou** usar o que já foi criado pelo backend na primeira subida.
3. Consumir mensagens JSON conforme o contrato acima.

## Resumo para implementar um listener

1. Conectar ao RabbitMQ (host, porta, vhost, usuário, senha).
2. Consumir da fila **`fila.eventos`**.
3. Tratar o corpo como **JSON** com os campos `id`, `title`, `location`, `event_date`, `group_id`.
4. Considerar **idempotência** e **falhas**: o backend pode não reenviar o mesmo lembrete após marcar `reminded`.
5. Opcional: inspecionar cabeçalhos `contentType` e `__TypeId__` se precisar depurar integração com Spring.

## Diagrama simplificado

```text
[PostgreSQL] <-- atualização reminded -- [DailyJob]
       ^
       |
  findUpcomingWithinSixHours()
       |
       v
[NotificationProducer] --(JSON)--> [Exchange default, rk=fila.eventos] --> [fila.eventos] --> [Seu listener / outro microserviço]
```
