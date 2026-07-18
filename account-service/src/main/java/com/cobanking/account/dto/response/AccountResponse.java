package com.cobanking.account.dto.response;

import com.cobanking.account.entity.AccountStatus;
import com.cobanking.account.entity.AccountType;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID accountId,
        UUID tenantId,
        UUID customerId,
        String accountNumber,
        AccountType accountType,
        String currency,
        AccountStatus status,
        Instant createdAt
) {
}
