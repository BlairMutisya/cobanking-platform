# Co-Banking Platform

Phase 1 foundation for a Spring Boot co-banking learning project with a custom ESB.

## Modules

- `common`: shared DTOs and error types.
- `api-gateway`: reactive gateway entry point.
- `auth-service`: authentication foundation.
- `customer-service`: customer profile foundation.
- `account-service`: account foundation.
- `transaction-service`: transaction request foundation.
- `ledger-service`: ledger posting foundation.
- `audit-service`: audit trail foundation.
- `esb`: custom integration layer foundation.

## Service Code Structure

Services use this package shape as they grow:

- `controller`: REST endpoints.
- `service`: service interfaces.
- `service.impl`: service implementations.
- `repository`: database access.
- `entity`: JPA entities.
- `enums`: service-specific enums.
- `dto.request`: request payloads.
- `dto.response`: response payloads.

All service endpoints return `BaseApiResponse<T>`:

```json
{
  "status": 1,
  "message": "Operation completed",
  "data": {}
}
```

## Audit Outbox Pattern

Services that produce audit events do not call `audit-service` directly inside the main business flow anymore. They save audit messages into a local `audit_outbox_events` table in the same database transaction as the business change.

Current producers:

- `account-service`: `ACCOUNT_OPENED`
- `transaction-service`: `TRANSFER_RECEIVED`, `TRANSFER_POSTED`, `TRANSFER_FAILED`
- `ledger-service`: `LEDGER_BATCH_POSTED`

Each producer has a scheduled publisher that retries pending outbox rows. This prevents losing audit events when `audit-service` is temporarily unavailable.

## JWT Security Foundation

The platform now starts its security model at the API gateway.

- `auth-service` issues signed JWT access tokens.
- `api-gateway` validates JWTs before forwarding business requests.
- Business endpoints require `Authorization: Bearer <token>` when called through `http://localhost:8080`.
- Swagger, health checks, and `POST /auth/login` stay public.

For now, users are demo users configured from `.env`. Later, this moves into `auth_db` with password hashing, refresh tokens, user lifecycle, and stronger authorization rules.

Demo usernames:

- `customer.demo`: role `CUSTOMER`
- `bank.admin`: role `BANK_ADMIN`
- `system.admin`: role `SYSTEM_ADMIN`

## Run Locally

Create your local environment file first:

```powershell
Copy-Item .env.example .env
```

Then open `.env` and fill in local-only database passwords, a JWT secret, and demo user passwords. `JWT_SECRET` must be at least 32 characters. Do not commit `.env`.

Start infrastructure:

```powershell
docker compose up -d
```

Build all modules:

```powershell
mvn clean package
```

Run one service:

```powershell
mvn -pl auth-service spring-boot:run
```

## Login Through The Gateway

Start `auth-service` and `api-gateway`, then request a token through the gateway:

```powershell
$login = Invoke-RestMethod -Method Post http://localhost:8080/auth/login `
  -ContentType "application/json" `
  -Body '{"username":"bank.admin","password":"<your DEMO_BANK_ADMIN_PASSWORD>"}'

$token = $login.data.accessToken
```

Call secured endpoints through `api-gateway` with the token:

```powershell
Invoke-RestMethod http://localhost:8080/accounts `
  -Headers @{Authorization="Bearer $token"}
```

Direct service ports are still useful while learning and debugging, but the production path is gateway first.

## Swagger / OpenAPI

Each service exposes generated API docs when it is running:

- Auth: `http://localhost:8081/swagger-ui.html`
- Customer: `http://localhost:8084/swagger-ui.html`
- Account: `http://localhost:8082/swagger-ui.html`
- Transaction: `http://localhost:8083/swagger-ui.html`
- Ledger: `http://localhost:8086/swagger-ui.html`
- Audit: `http://localhost:8085/swagger-ui.html`
- ESB: `http://localhost:8090/swagger-ui.html`

OpenAPI JSON is available at `/v3/api-docs` on each service.

The first goal is not full banking behavior yet. It is a clean, runnable foundation that we can grow safely.

## Phase 1B Sample Flow

Use UUIDs for public identifiers. A tenant represents a partner bank or institution.

Create a customer:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/customers `
  -Headers @{Authorization="Bearer $token"} `
  -ContentType "application/json" `
  -Body '{"tenantId":"11111111-1111-1111-1111-111111111111","fullName":"Amina Njeri","email":"amina@example.com","phoneNumber":"+254700000000"}'
```

Open an account:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/accounts `
  -Headers @{Authorization="Bearer $token"} `
  -ContentType "application/json" `
  -Body '{"tenantId":"11111111-1111-1111-1111-111111111111","customerId":"<customer UUID>","accountType":"SAVINGS","currency":"KES"}'
```

Validate an account:

```powershell
Invoke-RestMethod "http://localhost:8080/accounts/<account UUID>/validation?tenantId=11111111-1111-1111-1111-111111111111" `
  -Headers @{Authorization="Bearer $token"}
```

Record an audit event:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/audit/events `
  -Headers @{Authorization="Bearer $token"} `
  -ContentType "application/json" `
  -Body '{"tenantId":"11111111-1111-1111-1111-111111111111","actor":"system","action":"ACCOUNT_OPENED","resource":"ACCOUNT","resourceId":"<account UUID>","metadata":"{\"channel\":\"api\"}"}'
```

## Phase 1C Transfer + Ledger Flow

Transfers use an `Idempotency-Key` header. If the same request is retried with the same key, the transaction service returns the existing transaction instead of creating a duplicate.

Request a transfer:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/transactions/transfers `
  -Headers @{Authorization="Bearer $token"; "Idempotency-Key"="transfer-demo-001"} `
  -ContentType "application/json" `
  -Body '{"tenantId":"11111111-1111-1111-1111-111111111111","debitAccountId":"<debit account UUID>","creditAccountId":"<credit account UUID>","amount":250.00,"currency":"KES"}'
```

The transaction service validates both accounts first. It checks that each account exists, belongs to the same tenant, is active, and uses the same currency as the transfer. Then it stores the transfer request, records `TRANSFER_RECEIVED`, calls ledger service, and receives a ledger batch UUID when posting succeeds.

When ledger posting completes, transaction service records one of these audit events:

- `TRANSFER_POSTED`
- `TRANSFER_FAILED`

Fetch a transaction:

```powershell
Invoke-RestMethod "http://localhost:8080/transactions/<transaction UUID>?tenantId=11111111-1111-1111-1111-111111111111" `
  -Headers @{Authorization="Bearer $token"}
```

Fetch the ledger batch:

```powershell
Invoke-RestMethod "http://localhost:8080/ledger/batches/<ledger batch UUID>?tenantId=11111111-1111-1111-1111-111111111111" `
  -Headers @{Authorization="Bearer $token"}
```

The ledger service creates two entries for a transfer:

- `DEBIT` from the source account
- `CREDIT` to the destination account

The debit and credit totals must always balance before the batch is saved. After a balanced batch is committed, ledger service records a `LEDGER_BATCH_POSTED` audit event.
