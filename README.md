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

## Run Locally

Create your local environment file first:

```powershell
Copy-Item .env.example .env
```

Then open `.env` and fill in local-only database passwords. Do not commit `.env`.

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
Invoke-RestMethod -Method Post http://localhost:8084/customers `
  -ContentType "application/json" `
  -Body '{"tenantId":"11111111-1111-1111-1111-111111111111","fullName":"Amina Njeri","email":"amina@example.com","phoneNumber":"+254700000000"}'
```

Open an account:

```powershell
Invoke-RestMethod -Method Post http://localhost:8082/accounts `
  -ContentType "application/json" `
  -Body '{"tenantId":"11111111-1111-1111-1111-111111111111","customerId":"<customer UUID>","accountType":"SAVINGS","currency":"KES"}'
```

Record an audit event:

```powershell
Invoke-RestMethod -Method Post http://localhost:8085/audit/events `
  -ContentType "application/json" `
  -Body '{"tenantId":"11111111-1111-1111-1111-111111111111","actor":"system","action":"ACCOUNT_OPENED","resource":"ACCOUNT","resourceId":"<account UUID>","metadata":"{\"channel\":\"api\"}"}'
```

## Phase 1C Transfer + Ledger Flow

Transfers use an `Idempotency-Key` header. If the same request is retried with the same key, the transaction service returns the existing transaction instead of creating a duplicate.

Request a transfer:

```powershell
Invoke-RestMethod -Method Post http://localhost:8083/transactions/transfers `
  -Headers @{"Idempotency-Key"="transfer-demo-001"} `
  -ContentType "application/json" `
  -Body '{"tenantId":"11111111-1111-1111-1111-111111111111","debitAccountId":"<debit account UUID>","creditAccountId":"<credit account UUID>","amount":250.00,"currency":"KES"}'
```

The transaction service stores the transfer request, calls ledger service, and receives a ledger batch UUID when posting succeeds.

Fetch a transaction:

```powershell
Invoke-RestMethod "http://localhost:8083/transactions/<transaction UUID>?tenantId=11111111-1111-1111-1111-111111111111"
```

Fetch the ledger batch:

```powershell
Invoke-RestMethod "http://localhost:8086/ledger/batches/<ledger batch UUID>?tenantId=11111111-1111-1111-1111-111111111111"
```

The ledger service creates two entries for a transfer:

- `DEBIT` from the source account
- `CREDIT` to the destination account

The debit and credit totals must always balance before the batch is saved.
