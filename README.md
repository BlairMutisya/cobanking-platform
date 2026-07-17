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
