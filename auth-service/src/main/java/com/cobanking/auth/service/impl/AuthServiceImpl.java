package com.cobanking.auth.service.impl;

import com.cobanking.auth.dto.request.LoginRequest;
import com.cobanking.auth.dto.response.LoginResponse;
import com.cobanking.auth.enums.Role;
import com.cobanking.auth.service.AuthService;
import com.cobanking.common.exception.BusinessException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private static final UUID DEFAULT_TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long ttlMinutes;
    private final Map<String, DemoUser> demoUsers;

    public AuthServiceImpl(
            JwtEncoder jwtEncoder,
            @Value("${cobanking.security.jwt.issuer}") String issuer,
            @Value("${cobanking.security.jwt.ttl-minutes}") long ttlMinutes,
            @Value("${cobanking.security.demo-users.customer-password}") String customerPassword,
            @Value("${cobanking.security.demo-users.bank-admin-password}") String bankAdminPassword,
            @Value("${cobanking.security.demo-users.system-admin-password}") String systemAdminPassword
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
        this.demoUsers = Map.of(
                "customer.demo", new DemoUser(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "customer.demo", customerPassword, DEFAULT_TENANT_ID, List.of(Role.CUSTOMER.name())),
                "bank.admin", new DemoUser(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), "bank.admin", bankAdminPassword, DEFAULT_TENANT_ID, List.of(Role.BANK_ADMIN.name())),
                "system.admin", new DemoUser(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), "system.admin", systemAdminPassword, DEFAULT_TENANT_ID, List.of(Role.SYSTEM_ADMIN.name()))
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        DemoUser user = demoUsers.get(request.username());
        if (user == null || !matches(request.password(), user.password())) {
            throw new BusinessException("AUTH_INVALID_CREDENTIALS", "Invalid username or password");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(ttlMinutes, ChronoUnit.MINUTES);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.username())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("userId", user.userId().toString())
                .claim("tenantId", user.tenantId().toString())
                .claim("roles", user.roles())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new LoginResponse(user.userId(), user.username(), user.tenantId(), user.roles(), "Bearer", token, expiresAt);
    }

    private boolean matches(String providedPassword, String configuredPassword) {
        byte[] provided = providedPassword.getBytes(StandardCharsets.UTF_8);
        byte[] configured = configuredPassword.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(provided, configured);
    }

    private record DemoUser(UUID userId, String username, String password, UUID tenantId, List<String> roles) {
    }
}
