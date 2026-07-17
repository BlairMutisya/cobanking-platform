package com.cobanking.audit.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(
        UUID eventId,
        UUID tenantId,
        String actor,
        String action,
        String resource,
        UUID resourceId,
        String metadata,
        Instant recordedAt
) {
}
