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
