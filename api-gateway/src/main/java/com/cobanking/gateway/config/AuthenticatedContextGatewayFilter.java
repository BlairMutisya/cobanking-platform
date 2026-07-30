package com.cobanking.gateway.config;

import com.cobanking.common.security.SecurityHeaders;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticatedContextGatewayFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .ofType(JwtAuthenticationToken.class)
                .map(principal -> withAuthenticatedContext(exchange, principal.getToken()))
                .defaultIfEmpty(stripAuthenticatedContext(exchange))
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private ServerWebExchange withAuthenticatedContext(ServerWebExchange exchange, Jwt jwt) {
        String userId = jwt.getClaimAsString("userId");
        String tenantId = jwt.getClaimAsString("tenantId");
        List<String> roles = jwt.getClaimAsStringList("roles");
        String rolesHeader = roles == null ? "" : String.join(",", roles);

        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    removeAuthenticatedContext(headers);
                    headers.set(SecurityHeaders.AUTHENTICATED_USER_ID, userId);
                    headers.set(SecurityHeaders.AUTHENTICATED_USERNAME, jwt.getSubject());
                    headers.set(SecurityHeaders.AUTHENTICATED_TENANT_ID, tenantId);
                    headers.set(SecurityHeaders.AUTHENTICATED_ROLES, rolesHeader);
                })
                .build();

        return exchange.mutate().request(request).build();
    }

    private ServerWebExchange stripAuthenticatedContext(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(this::removeAuthenticatedContext)
                .build();
        return exchange.mutate().request(request).build();
    }

    private void removeAuthenticatedContext(HttpHeaders headers) {
        headers.remove(SecurityHeaders.AUTHENTICATED_USER_ID);
        headers.remove(SecurityHeaders.AUTHENTICATED_USERNAME);
        headers.remove(SecurityHeaders.AUTHENTICATED_TENANT_ID);
        headers.remove(SecurityHeaders.AUTHENTICATED_ROLES);
    }
}
