# API Church Backend — exemplos com `curl`

Base URL padrão: `http://localhost:8080`. Todos os endpoints (exceto **login** e **registro**) exigem header:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Sugestão de variáveis no shell:

```bash
export BASE=http://localhost:8080
export TOKEN='cole_aqui_o_accessToken_retornado_no_login'
```

Para guardar o token após o login (requer [jq](https://jqlang.org/)):

```bash
export TOKEN=$(curl -s -X POST "$BASE/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@church.local","password":"admin123"}' | jq -r '.accessToken')
echo "$TOKEN"
```

---

## 1. Autenticação

### `POST /api/auth/login`

Não requer `Authorization`.

```bash
curl -s -X POST "$BASE/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "admin@church.local",
    "password": "admin123"
  }'
```

Resposta esperada (exemplo):

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### `POST /api/auth/register`

Cria conta pública com papel **MEMBER** (sem grupo; um **ADMIN** pode associar depois com `PUT /api/users/{id}/group`). Resposta **201** com o mesmo formato do login (JWT para usar já autenticado).

```bash
curl -s -w "\n%{http_code}\n" -X POST "$BASE/api/auth/register" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Novo Membro",
    "email": "novo.membro@example.com",
    "password": "senha123",
    "phone": "85999991111"
  }'
```

O campo `phone` é opcional (pode omitir ou enviar `null`).

---

## 2. Igrejas (Church) — papel **ADMIN**

### `POST /api/churches`

```bash
curl -s -X POST "$BASE/api/churches" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Igreja Comunidade Esperança"
  }'
```

### `GET /api/churches`

```bash
curl -s "$BASE/api/churches" \
  -H "Authorization: Bearer $TOKEN"
```

### `GET /api/churches/{id}`

```bash
curl -s "$BASE/api/churches/1" \
  -H "Authorization: Bearer $TOKEN"
```

### `PUT /api/churches/{id}`

```bash
curl -s -X PUT "$BASE/api/churches/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Igreja Comunidade Esperança (atualizada)"
  }'
```

### `DELETE /api/churches/{id}`

Soft delete (`active = false`).

```bash
curl -s -o /dev/null -w "%{http_code}\n" -X DELETE "$BASE/api/churches/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 3. Grupos (Group)

### `POST /api/churches/{churchId}/groups` — **ADMIN**

```bash
curl -s -X POST "$BASE/api/churches/1/groups" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Célula Centro",
    "description": "Reuniões às quartas, 20h"
  }'
```

### `GET /api/churches/{churchId}/groups` — **ADMIN** ou **LEADER** (com escopo na igreja)

```bash
curl -s "$BASE/api/churches/1/groups" \
  -H "Authorization: Bearer $TOKEN"
```

### `GET /api/groups/{id}` — **ADMIN** ou **LEADER**

```bash
curl -s "$BASE/api/groups/1" \
  -H "Authorization: Bearer $TOKEN"
```

### `PUT /api/groups/{id}` — **ADMIN**

```bash
curl -s -X PUT "$BASE/api/groups/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Célula Centro — Zona Sul",
    "description": "Novo horário: 19h30"
  }'
```

### `PUT /api/groups/{id}/leader` — **ADMIN**

Definir líder (`leaderUserId`) ou remover líder (`null`).

```bash
curl -s -X PUT "$BASE/api/groups/1/leader" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "leaderUserId": 2
  }'
```

Remover líder do grupo:

```bash
curl -s -X PUT "$BASE/api/groups/1/leader" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "leaderUserId": null
  }'
```

### `DELETE /api/groups/{id}` — **ADMIN**

```bash
curl -s -o /dev/null -w "%{http_code}\n" -X DELETE "$BASE/api/groups/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 4. Usuários (User)

### `POST /api/users` — **ADMIN**

```bash
curl -s -X POST "$BASE/api/users" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Maria Silva",
    "email": "maria.silva@example.com",
    "password": "senha123",
    "phone": "85999990000",
    "role": "MEMBER",
    "groupId": 1
  }'
```

Criar **líder** (sem grupo inicial; depois associe com `PUT .../leader`):

```bash
curl -s -X POST "$BASE/api/users" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "João Líder",
    "email": "joao.lider@example.com",
    "password": "senha123",
    "role": "LEADER"
  }'
```

### `GET /api/users` — **ADMIN**

```bash
curl -s "$BASE/api/users" \
  -H "Authorization: Bearer $TOKEN"
```

### `GET /api/users/{id}` — **ADMIN** ou **LEADER** (escopo)

```bash
curl -s "$BASE/api/users/2" \
  -H "Authorization: Bearer $TOKEN"
```

### `GET /api/groups/{groupId}/users` — **ADMIN** ou **LEADER**

```bash
curl -s "$BASE/api/groups/1/users" \
  -H "Authorization: Bearer $TOKEN"
```

### `PUT /api/users/{id}` — **ADMIN**

```bash
curl -s -X PUT "$BASE/api/users/2" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Maria Silva Santos",
    "email": "maria.silva@example.com",
    "phone": "85988887777",
    "role": "MEMBER",
    "password": ""
  }'
```

> Para **não** alterar a senha, envie `password` vazio ou omita no cliente; no exemplo acima string vazia não altera o hash no servidor.

### `PUT /api/users/{id}/group` — **ADMIN**

```bash
curl -s -X PUT "$BASE/api/users/2/group" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "groupId": 1
  }'
```

### `DELETE /api/users/{id}` — **ADMIN**

```bash
curl -s -o /dev/null -w "%{http_code}\n" -X DELETE "$BASE/api/users/2" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 5. Eventos (Event)

### `POST /api/groups/{groupId}/events` — **ADMIN** ou **LEADER** (líder do grupo)

```bash
curl -s -X POST "$BASE/api/groups/1/events" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Culto de celebração",
    "location": "Templo principal",
    "eventDate": "2026-03-30T19:00:00Z",
    "status": "SCHEDULED"
  }'
```

Valores de `status`: `SCHEDULED`, `OPEN`, `CLOSED`. Se omitir `status`, o padrão é `SCHEDULED`.

### `GET /api/groups/{groupId}/events` — **ADMIN** ou **LEADER**

```bash
curl -s "$BASE/api/groups/1/events" \
  -H "Authorization: Bearer $TOKEN"
```

### `GET /api/events/{id}` — **ADMIN** ou **LEADER**

```bash
curl -s "$BASE/api/events/1" \
  -H "Authorization: Bearer $TOKEN"
```

### `PUT /api/events/{id}` — **ADMIN** ou **LEADER**

Abrir evento para check-in (`OPEN`):

```bash
curl -s -X PUT "$BASE/api/events/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Culto de celebração",
    "location": "Templo principal",
    "eventDate": "2026-03-30T19:00:00Z",
    "status": "OPEN"
  }'
```

### `DELETE /api/events/{id}` — **ADMIN** ou **LEADER**

```bash
curl -s -o /dev/null -w "%{http_code}\n" -X DELETE "$BASE/api/events/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 6. Presença (Presence)

### `POST /api/events/{eventId}/checkin` — **MEMBER**

Corpo vazio. O usuário autenticado é quem faz check-in. O evento deve estar com `status` **OPEN**, e o membro deve pertencer ao **mesmo grupo** do evento.

```bash
export TOKEN_MEMBRO='token_jwt_do_usuario_com_role_MEMBER_no_grupo_do_evento'

curl -s -X POST "$BASE/api/events/1/checkin" \
  -H "Authorization: Bearer $TOKEN_MEMBRO" \
  -H 'Content-Type: application/json' \
  -d '{}'
```

Segundo check-in no mesmo evento retorna **409 Conflict** (`Check-in já registrado`).

### `GET /api/events/{eventId}/presences` — **ADMIN** ou **LEADER** (líder do grupo do evento)

```bash
curl -s "$BASE/api/events/1/presences" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 7. Erros (formato JSON)

Exemplos de resposta de erro:

```json
{
  "timestamp": "2026-03-21T22:00:00.123456789Z",
  "status": 403,
  "error": "FORBIDDEN",
  "message": "Sem permissão"
}
```

Validação (`400`):

```json
{
  "timestamp": "2026-03-21T22:00:00.123456789Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "email: must be a well-formed email address"
}
```

---

## 8. Dados mock do seed (Flyway `V2__seed_dev.sql`)

| Item        | Valor (exemplo)        |
|------------|-------------------------|
| Igreja     | `id = 1`                |
| Grupo      | `id = 1`                |
| Admin      | `admin@church.local` / `admin123` |

Com apenas o seed, use o **admin** para criar usuários **MEMBER** no grupo `1`, abrir um evento como **OPEN** e então testar check-in com o token do membro.

---

## 9. CORS

O backend permite origem `http://localhost:4200` (Angular). Chamadas `curl` não sofrem com CORS.
