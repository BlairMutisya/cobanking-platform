package com.cobanking.account.dto.response;

import com.cobanking.account.enums.AccountStatus;
import java.util.UUID;

public record AccountValidationResponse(
        UUID accountId,
        UUID tenantId,
        String currency,
        AccountStatus status,
        boolean valid,
        String reason
) {
}
