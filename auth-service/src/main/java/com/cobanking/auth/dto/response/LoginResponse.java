package com.cobanking.auth.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LoginResponse(
        String username,
        UUID tenantId,
        List<String> roles,
        String tokenType,
        String accessToken,
        Instant expiresAt
) {
}
