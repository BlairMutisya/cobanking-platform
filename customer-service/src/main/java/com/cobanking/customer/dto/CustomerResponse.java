package com.cobanking.customer.dto;

import com.cobanking.customer.domain.CustomerStatus;
import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID customerId,
        UUID tenantId,
        String fullName,
        String email,
        String phoneNumber,
        CustomerStatus status,
        Instant createdAt
) {
}
