package com.cobanking.common.security;

import com.cobanking.common.exception.BusinessException;
import java.util.UUID;

public final class RequestSecurity {
    private RequestSecurity() {
    }

    public static void requireTenantMatch(UUID requestTenantId, String authenticatedTenantId) {
        if (authenticatedTenantId == null || authenticatedTenantId.isBlank()) {
            return;
        }

        UUID tokenTenantId;
        try {
            tokenTenantId = UUID.fromString(authenticatedTenantId);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("SECURITY_INVALID_TENANT_CONTEXT", "Authenticated tenant context is invalid");
        }

        if (!tokenTenantId.equals(requestTenantId)) {
            throw new BusinessException("SECURITY_TENANT_MISMATCH", "Request tenant does not match authenticated tenant");
        }
    }
}
