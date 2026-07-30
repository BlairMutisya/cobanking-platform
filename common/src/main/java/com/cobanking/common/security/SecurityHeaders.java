package com.cobanking.common.security;

public final class SecurityHeaders {
    public static final String AUTHENTICATED_USER_ID = "X-Authenticated-User-Id";
    public static final String AUTHENTICATED_USERNAME = "X-Authenticated-Username";
    public static final String AUTHENTICATED_TENANT_ID = "X-Authenticated-Tenant-Id";
    public static final String AUTHENTICATED_ROLES = "X-Authenticated-Roles";

    private SecurityHeaders() {
    }
}
