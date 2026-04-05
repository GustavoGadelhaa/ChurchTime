# ChurchTime - Documentação para o Frontend

## Visão Geral

**ChurchTime** é um backend em Java/Spring Boot para gerenciamento de presença em eventos de igrejas. O sistema organiza igrejas, grupos (células/ministérios), usuários e eventos com controle de check-in.

### Tech Stack

| Camada | Tecnologia |
|---|---|
| Backend | Java 21 + Spring Boot 3.4.5 |
| Banco de Dados | PostgreSQL 16 |
| Autenticação | JWT (HS256) + Spring Security |
| Mensageria | RabbitMQ |
| Frontend | Angular 18+ (localhost:4200) |

### URL Base

- **Dev:** `http://localhost:8080/api`
- **Prod:** `https://api.churchtime.com.br/api`

### Autenticação

Todas as rotas (exceto login, registro e recuperação de senha) exigem o header:

```
Authorization: Bearer <JWT_TOKEN>
```

O token é retornado no login/registro no campo `accessToken`. Armazene em `localStorage`.

---

## Hierarquia de Dados

```
Church (1) ────< (N) Group (1) ────< (N) User
                        │                    │
                        │                    └───< (N) Presence
                        │                           ▲
                        └───< (N) Event ────────────┘
```

### Roles

| Role | O que pode fazer |
|---|---|
| `ADMIN` | Acesso total a tudo |
| `LEADER` | CRUD nos grupos que lidera, eventos e presenças |
| `MEMBER` | Ver grupos da sua igreja, trocar de grupo, check-in em eventos OPEN |

### Status de Evento

| Status | Significado |
|---|---|
| `SCHEDULED` | Agendado, ainda não aberto |
| `OPEN` | Aberto para check-in |
| `CLOSED` | Encerrado |

---

## Endpoints da API

### Autenticação (público — sem token)

| Método | Path | Descrição | Body | Resposta |
|---|---|---|---|---|
| `POST` | `/api/auth/login` | Login | `{ email, password }` | `{ accessToken }` |
| `POST` | `/api/auth/register` | Auto-registro (MEMBER) | `{ name, email, password, phone? }` | `{ accessToken }` |
| `POST` | `/api/auth/forgot-password` | Solicitar código de recuperação | `{ email }` | `200` (vazio) |
| `POST` | `/api/auth/reset-password` | Redefinir senha com código | `{ token, newPassword }` | `200` (vazio) |

### Igrejas (ADMIN)

| Método | Path | Descrição | Body |
|---|---|---|---|
| `POST` | `/api/churches` | Criar igreja | `{ name }` |
| `GET` | `/api/churches` | Listar igrejas ativas | — |
| `GET` | `/api/churches/{id}` | Buscar igreja | — |
| `PUT` | `/api/churches/{id}` | Atualizar igreja | `{ name }` |
| `DELETE` | `/api/churches/{id}` | Soft delete | — |

### Grupos (ADMIN, LEADER)

| Método | Path | Descrição | Body | Auth |
|---|---|---|---|---|
| `POST` | `/api/churches/{churchId}/groups` | Criar grupo | `{ name, description? }` | ADMIN |
| `GET` | `/api/churches/{churchId}/groups` | Listar grupos da igreja | — | ADMIN, LEADER |
| `GET` | `/api/groups/{id}` | Buscar grupo | — | ADMIN, LEADER |
| `PUT` | `/api/groups/{id}` | Atualizar grupo | `{ name, description? }` | ADMIN |
| `PUT` | `/api/groups/{id}/leader` | Atribuir/remover líder | `{ leaderUserId }` | ADMIN |
| `DELETE` | `/api/groups/{id}` | Soft delete | — | ADMIN |

### Grupos — Acesso do Membro (qualquer role autenticada)

| Método | Path | Descrição | Body |
|---|---|---|---|
| `GET` | `/api/groups/my-church` | Listar todos os grupos ativos da igreja do usuário | — |
| `PUT` | `/api/groups/{id}/join` | Membro entra em outro grupo da mesma igreja | — |

**Regras do join:**
- Apenas role `MEMBER` pode usar
- O membro já precisa ter um grupo associado (para identificar a igreja)
- O grupo de destino deve estar ativo e pertencer à mesma igreja

### Usuários (ADMIN)

| Método | Path | Descrição | Body |
|---|---|---|---|
| `POST` | `/api/users` | Criar usuário | `{ name, email, password, phone?, role, groupId? }` |
| `GET` | `/api/users` | Listar usuários ativos | — |
| `GET` | `/api/users/{id}` | Buscar usuário | — |
| `GET` | `/api/groups/{groupId}/users` | Listar usuários do grupo | — |
| `PUT` | `/api/users/{id}` | Atualizar usuário | `{ name, email, phone?, role, password? }` |
| `PUT` | `/api/users/{id}/group` | Mover usuário de grupo | `{ groupId }` |
| `DELETE` | `/api/users/{id}` | Soft delete | — |

### Eventos (ADMIN, LEADER)

| Método | Path | Descrição | Body |
|---|---|---|---|
| `POST` | `/api/groups/{groupId}/events` | Criar evento | `{ title, location?, eventDate, status? }` |
| `GET` | `/api/groups/{groupId}/events` | Listar eventos do grupo | — |
| `GET` | `/api/events/{id}` | Buscar evento | — |
| `PUT` | `/api/events/{id}` | Atualizar evento | `{ title, location?, eventDate, status? }` |
| `DELETE` | `/api/events/{id}` | Hard delete | — |

### Presença / Check-in

| Método | Path | Descrição | Body | Auth |
|---|---|---|---|---|
| `POST` | `/api/events/{eventId}/checkin` | Fazer check-in | `{}` (vazio) | MEMBER |
| `GET` | `/api/events/{eventId}/presences` | Listar presenças | — | ADMIN, LEADER |

---

## Interfaces TypeScript

```typescript
type UserRole = 'ADMIN' | 'LEADER' | 'MEMBER';
type EventStatus = 'SCHEDULED' | 'OPEN' | 'CLOSED';

// Auth
interface AuthResponse { accessToken: string }
interface LoginRequest { email: string; password: string }
interface RegisterRequest { name: string; email: string; password: string; phone?: string }
interface ForgotPasswordRequest { email: string }
interface ResetPasswordRequest { token: string; newPassword: string }

// Church
interface ChurchResponse { id: number; name: string; active: boolean; createdAt: string }

// Group
interface GroupResponse {
  id: number; churchId: number; leaderId: number | null;
  name: string; description: string | null; active: boolean; createdAt: string;
}

// User
interface UserResponse {
  id: number; groupId: number | null; name: string; email: string;
  phone: string | null; role: UserRole; active: boolean;
  createdAt: string; updatedAt: string;
}

// Event
interface EventResponse {
  id: number; groupId: number; title: string; location: string | null;
  eventDate: string; status: EventStatus; createdAt: string;
}

// Presence
interface PresenceResponse {
  id: number; eventId: number; userId: number; userName: string; checkedInAt: string;
}
```

---

## Fluxos de Tela por Role

**ADMIN:** Dashboard completo → CRUD de Churches, Groups, Users, Events, Presence

**LEADER:** Dashboard → Groups que lidera, Events, Presence (apenas dos seus grupos)

**MEMBER:** Dashboard → Ver todos os grupos da igreja (`GET /api/groups/my-church`), trocar de grupo (`PUT /api/groups/{id}/join`), check-in em eventos OPEN do seu grupo

---

## Recuperação de Senha — Fluxo Detalhado

Veja o documento completo em [FEATURE-RECUPERACAO-SENHA.md](./FEATURE-RECUPERACAO-SENHA.md).

**Resumo:**
1. `POST /api/auth/forgot-password` → envia código de 6 dígitos por e-mail (válido por 15min)
2. `POST /api/auth/reset-password` → valida código e redefine senha
3. Redirecionar para `/password-reset-success` → botão "Fazer login"

---

## Configuração do CORS

O backend já permite `http://localhost:4200` em desenvolvimento.

---

## Documentação Interativa (Swagger)

Com o backend rodando:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

É possível gerar clients TypeScript automaticamente:

```bash
npx @openapitools/openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-angular \
  -o ./src/app/core/generated
```

---

## O que falta para ser um SaaS sustentável

### Roadmap

| Prioridade | Item | Esforço | Impacto |
|---|---|---|---|
| **P0** | Multi-tenancy | Alto | Crítico |
| **P0** | Frontend (web + mobile/PWA) | Alto | Crítico |
| **P0** | Sistema de pagamentos | Médio | Crítico |
| **P0** | Onboarding self-service | Médio | Crítico |
| **P1** | Verificação de email no registro | Baixo | Alto |
| **P1** | Notificações funcionais (email/push) | Médio | Alto |
| **P1** | Refresh tokens | Baixo | Alto |
| **P1** | Eventos recorrentes | Médio | Alto |
| **P1** | Relatórios básicos | Médio | Alto |
| **P2** | QR Code check-in | Médio | Médio |
| **P2** | Testes automatizados | Alto | Médio |
| **P2** | CI/CD + deploy cloud | Médio | Médio |
| **P2** | LGPD | Médio | Médio |
