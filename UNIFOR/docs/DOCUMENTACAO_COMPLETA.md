# ChurchTime - Documentação Completa

## Visão Geral

**ChurchTime** (Church Presence MVP) é um backend em Java/Spring Boot para gerenciamento de presença em eventos de igrejas. O sistema organiza igrejas, grupos (células/ministérios), usuários e eventos com controle de check-in.

### Tech Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4.5 |
| Banco de Dados | PostgreSQL 16 |
| ORM | Spring Data JPA (Hibernate) |
| Migrações | Flyway |
| Autenticação | JWT (HS256) + Spring Security |
| Mensageria | RabbitMQ |
| Build | Maven |
| Containerização | Docker Compose |
| Frontend esperado | Angular (localhost:4200) |

---

## Hierarquia de Dados

```
Church (1) ────< (N) Group (1) ────< (N) User
                        │                    │
                        │                    └───< (N) Presence
                        │                           ▲
                        └───< (N) Event ────────────┘
```

### Modelos

| Entidade | Tabela | Campos Principais |
|---|---|---|
| **Church** | `churches` | id, name, active, created_at |
| **Group** | `groups` | id, church_id, leader_id, name, description, active, created_at |
| **User** | `users` | id, group_id, name, email, password_hash, phone, role, active, created_at, updated_at |
| **Event** | `events` | id, group_id, title, location, event_date, status, created_at, reminded |
| **Presence** | `presences` | id, event_id, user_id, checked_in_at |

### Roles

| Role | Escopo |
|---|---|
| `ADMIN` | Acesso total a tudo |
| `LEADER` | CRUD nos grupos que lidera, eventos e presenças associadas |
| `MEMBER` | Check-in em eventos OPEN do seu grupo |

### Status de Evento

| Status | Significado |
|---|---|
| `SCHEDULED` | Evento agendado, ainda não aberto |
| `OPEN` | Evento aberto para check-in |
| `CLOSED` | Evento encerrado |

---

## Listagem Completa de Endpoints

### Autenticação (público)

| Método | Path | Descrição | Body | Resposta |
|---|---|---|---|---|
| `POST` | `/api/auth/login` | Login com email/senha | `{ email, password }` | `{ token }` |
| `POST` | `/api/auth/register` | Auto-registro (role=MEMBER) | `{ name, email, password, phone }` | `{ token }` |

---

### Igrejas (ADMIN)

| Método | Path | Descrição | Body | Auth |
|---|---|---|---|---|
| `POST` | `/api/churches` | Criar igreja | `{ name }` | ADMIN |
| `GET` | `/api/churches` | Listar igrejas ativas | — | ADMIN |
| `GET` | `/api/churches/{id}` | Buscar igreja por ID | — | ADMIN |
| `PUT` | `/api/churches/{id}` | Atualizar nome da igreja | `{ name }` | ADMIN |
| `DELETE` | `/api/churches/{id}` | Soft delete da igreja | — | ADMIN |

---

### Grupos (células/ministérios)

| Método | Path | Descrição | Body | Auth |
|---|---|---|---|---|
| `POST` | `/api/churches/{churchId}/groups` | Criar grupo | `{ name, description }` | ADMIN |
| `GET` | `/api/churches/{churchId}/groups` | Listar grupos da igreja | — | ADMIN, LEADER |
| `GET` | `/api/groups/{id}` | Buscar grupo por ID | — | ADMIN, LEADER |
| `PUT` | `/api/groups/{id}` | Atualizar grupo | `{ name, description }` | ADMIN |
| `PUT` | `/api/groups/{id}/leader` | Atribuir/remover líder | `{ leaderId }` | ADMIN |
| `DELETE` | `/api/groups/{id}` | Soft delete do grupo | — | ADMIN |

---

### Usuários

| Método | Path | Descrição | Body | Auth |
|---|---|---|---|---|
| `POST` | `/api/users` | Criar usuário | `{ name, email, password, phone, role, groupId }` | ADMIN |
| `GET` | `/api/users` | Listar usuários ativos | — | ADMIN |
| `GET` | `/api/users/{id}` | Buscar usuário por ID | — | ADMIN, LEADER |
| `GET` | `/api/groups/{groupId}/users` | Listar usuários do grupo | — | ADMIN, LEADER |
| `PUT` | `/api/users/{id}` | Atualizar usuário | `{ name, email, phone, role, groupId }` | ADMIN |
| `PUT` | `/api/users/{id}/group` | Mover usuário de grupo | `{ groupId }` | ADMIN |
| `DELETE` | `/api/users/{id}` | Soft delete do usuário | — | ADMIN |

---

### Eventos

| Método | Path | Descrição | Body | Auth |
|---|---|---|---|---|
| `POST` | `/api/groups/{groupId}/events` | Criar evento | `{ title, location, eventDate, status }` | ADMIN, LEADER |
| `GET` | `/api/groups/{groupId}/events` | Listar eventos do grupo | — | ADMIN, LEADER |
| `GET` | `/api/events/{id}` | Buscar evento por ID | — | ADMIN, LEADER |
| `PUT` | `/api/events/{id}` | Atualizar evento | `{ title, location, eventDate, status }` | ADMIN, LEADER |
| `DELETE` | `/api/events/{id}` | Deletar evento (hard delete) | — | ADMIN, LEADER |

---

### Presença / Check-in

| Método | Path | Descrição | Body | Auth |
|---|---|---|---|---|
| `POST` | `/api/events/{eventId}/checkin` | Fazer check-in no evento | `{}` (vazio) | MEMBER |
| `GET` | `/api/events/{eventId}/presences` | Listar presenças do evento | — | ADMIN, LEADER |

---

### Mensageria Admin (debug)

| Método | Path | Descrição | Body | Auth |
|---|---|---|---|---|
| `POST` | `/api/admin/messaging/events` | Publicar evento manualmente no RabbitMQ | `{ eventId }` | ADMIN |

---

## O que falta para ser um SaaS sustentável

### 1. Multi-tenancy Real

**Problema:** O modelo atual assume uma única igreja gerenciada por um ADMIN global. Num SaaS, cada igreja é um tenant isolado.

**O que fazer:**
- Adicionar `tenant_id` em todas as tabelas ou usar schema por tenant
- Criar entidade `Organization`/`Tenant` que representa a igreja cliente do SaaS
- Administrador da igreja = `ORG_ADMIN` (diferente de `ADMIN` global da plataforma)
- Isolar dados: queries sempre filtrar por tenant
- Subdomínio por igreja (`igreja1.churchtime.com.br`)

---

### 2. Sistema de Assinatura / Pagamentos

**Problema:** Não existe nenhum mecanismo de cobrança.

**O que fazer:**
- Integração com gateway de pagamento (Stripe, Mercado Pago, Asaas)
- Planos: Gratuito (limitado), Pro, Enterprise
- Controle de assinatura por tenant (status, data de expiração, plano)
- Webhooks para pagamento recorrente, falha, cancelamento
- Bloqueio progressivo de funcionalidades quando assinatura expira
- Histórico de faturas/invoices
- Trial period (ex: 14 dias grátis)

---

### 3. Frontend

**Problema:** Não existe frontend. CORS está configurado para Angular em localhost:4200, mas nada foi construído.

**O que fazer:**
- Dashboard web responsivo (Angular, React ou Vue)
- App mobile (React Native, Flutter) ou PWA
- Páginas públicas de evento para check-in via QR Code
- Landing page de marketing + página de preços
- Portal de autoatendimento da igreja (cadastro sem intervenção manual)

---

### 4. Onboarding Self-Service

**Problema:** Igrejas não conseguem se cadastrar sozinhas. Tudo é feito via ADMIN.

**O que fazer:**
- Fluxo de cadastro completo: criar conta da igreja → configurar dados → convidar membros
- Wizard de onboarding com passos guiados
- Importação de membros via CSV/Excel
- Templates de eventos pré-configurados (culto domingo, ensaio, célula)

---

### 5. Autenticação e Segurança

**Problema:** JWT HS256 com secret configurável, sem refresh token, sem 2FA, sem recuperação de senha.

**O que fazer:**
- Refresh tokens com rotação
- Recuperação de senha por email (Spring Mail já está como dependência, mas não usado)
- Verificação de email no registro
- 2FA (TOTP)
- Rate limiting nos endpoints de autenticação
- Audit log (quem fez o quê e quando)
- Proteção contra brute force (bloqueio após N tentativas)
- Expirar sessões em mudança de senha

---

### 6. Notificações

**Problema:** Existe RabbitMQ para lembretes de eventos, mas não há consumidor real de notificações.

**O que fazer:**
- Notificações por email (confirmar check-in, lembrete de evento, resumo semanal)
- Notificações push (se houver app mobile)
- Notificações por WhatsApp/SMS (integração com Twilio ou similar)
- Template de emails customizáveis por igreja
- Preferências de notificação por usuário

---

### 7. Relatórios e Analytics

**Problema:** Não há nenhuma funcionalidade de relatórios.

**O que fazer:**
- Dashboard com métricas: frequência média, tendência, eventos mais frequentados
- Exportar relatórios em PDF/CSV
- Gráficos de presença ao longo do tempo
- Comparativo entre grupos/ministérios
- Métricas de engajamento por membro
- Relatório semanal/mensal automático por email para líderes

---

### 8. Funcionalidades de Evento

**Problema:** Eventos são básicos (título, local, data, status).

**O que fazer:**
- Eventos recorrentes (semanal, quinzenal, mensal)
- Capacidade máxima do evento (limite de vagas)
- Lista de espera
- Check-in por QR Code (gerar QR por evento, membro escaneia ou mostra QR pessoal)
- Check-in tardio com justificativa
- Categorias de evento (culto, célula, retiro, ensaio)
- Descrição longa do evento
- Anexos (arquivos, imagens)
- Feedback pós-evento (avaliação)

---

### 9. Gestão de Membros

**Problema:** Usuários são simples com nome, email, telefone e grupo.

**O que fazer:**
- Perfil completo do membro (foto, data de nascimento, endereço, data de batismo)
- Histórico de presença individual
- Tags/categorias de membros (novo, visitante, regular, líder)
- Fluxo de visitantes → membro efetivo
- Aniversariantes da semana
- Comunicação em massa para segmentos de membros

---

### 10. Infraestrutura e DevOps

**Problema:** Só existe docker-compose local, sem pipeline de CI/CD, sem monitoramento.

**O que fazer:**
- CI/CD (GitHub Actions, GitLab CI)
- Deploy em cloud (AWS, GCP, Render, Railway)
- Monitoramento e alertas (Sentry, Datadog, Grafana)
- Logging estruturado centralizado
- Health checks e readiness/liveness probes
- Backup automatizado do banco
- Variáveis de ambiente por ambiente (.env.example)
- Documentação de deploy
- Escalabilidade horizontal (stateless, connection pooling)

---

### 11. API e Integrações

**Problema:** API básica sem versionamento, sem documentação OpenAPI, sem webhooks.

**O que fazer:**
- Versionamento de API (`/api/v1/...`)
- Documentação OpenAPI/Swagger (springdoc-openapi)
- Webhooks para integrações externas (ex: notificar sistema da igreja quando alguém faz check-in)
- API pública com rate limiting e API keys
- SDK ou bibliotecas cliente

---

### 12. Testes

**Problema:** Só existe um teste básico de aplicação (`ChurchBackendApplicationTests`).

**O que fazer:**
- Testes unitários de services
- Testes de integração de controllers com MockMvc
- Testes de segurança (acesso negado para roles erradas)
- Testes de repositório
- Testes end-to-end
- Cobertura mínima de 80%

---

### 13. Funcionalidades Administrativas da Plataforma (Super Admin)

**Problema:** Não existe distinção entre admin da plataforma e admin da igreja.

**O que fazer:**
- Painel Super Admin para gerenciar todas as igrejas clientes
- Métricas da plataforma (total de igrejas, MRR, churn)
- Gestão de planos e assinaturas
- Suporte integrado (tickets)
- Feature flags para liberar funcionalidades por plano

---

### 14. Conformidade Legal (LGPD)

**Problema:** Nenhuma consideração de privacidade.

**O que fazer:**
- Consentimento explícito no registro
- Política de privacidade e termos de uso
- Exportação de dados pessoais do usuário
- Direito ao esquecimento (deleção completa)
- Registro de consentimento
- DPO contact

---

### 15. Internacionalização (i18n)

**Problema:** Mensagens de erro e dados em inglês, público-alvo é brasileiro.

**O que fazer:**
- Suporte a português brasileiro como idioma padrão
- Mensagens de erro localizadas
- Formatação de datas, horários e telefones no padrão BR
- Preparar estrutura para múltiplos idiomas

---

## Resumo de Prioridades

| Prioridade | Item | Esforço | Impacto |
|---|---|---|---|
| **P0** | Multi-tenancy | Alto | Crítico |
| **P0** | Frontend (web + mobile/PWA) | Alto | Crítico |
| **P0** | Sistema de pagamentos | Médio | Crítico |
| **P0** | Onboarding self-service | Médio | Crítico |
| **P1** | Recuperação de senha + verificação email | Baixo | Alto |
| **P1** | Notificações funcionais (email/push) | Médio | Alto |
| **P1** | Refresh tokens | Baixo | Alto |
| **P1** | Eventos recorrentes | Médio | Alto |
| **P1** | Relatórios básicos | Médio | Alto |
| **P2** | QR Code check-in | Médio | Médio |
| **P2** | Testes automatizados | Alto | Médio |
| **P2** | CI/CD + deploy cloud | Médio | Médio |
| **P2** | OpenAPI/Swagger | Baixo | Médio |
| **P2** | LGPD | Médio | Médio |
| **P3** | Analytics avançado | Alto | Baixo |
| **P3** | Webhooks e integrações | Médio | Baixo |
| **P3** | Internacionalização | Baixo | Baixo |
| **P3** | Super Admin dashboard | Médio | Baixo |

---

## Conclusão

O projeto atual é um **MVP funcional** com uma base sólida (Spring Boot, JWT, RabbitMQ, PostgreSQL). Porém, para se tornar um **SaaS sustentável**, precisa de transformações fundamentais:

1. **Arquitetura multi-tenant** — sem isso, não é SaaS
2. **Monetização** — sem pagamentos, não é sustentável
3. **Frontend** — sem interface, não é utilizável
4. **Self-service** — sem onboarding automático, não escala

O backend atual cobre bem o domínio de eventos e presença, mas precisa de ~15 grandes adições para competir no mercado de software para igrejas.
