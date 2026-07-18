package com.cobanking.audit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RecordAuditEventRequest(
        @NotNull UUID tenantId,
        @NotBlank String actor,
        @NotBlank String action,
        @NotBlank String resource,
        UUID resourceId,
        String metadata
) {
}
