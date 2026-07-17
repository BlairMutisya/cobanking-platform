package com.cobanking.account.dto;

import com.cobanking.account.domain.AccountStatus;
import com.cobanking.account.domain.AccountType;
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
