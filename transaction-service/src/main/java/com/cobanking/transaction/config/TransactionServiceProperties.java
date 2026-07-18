package com.cobanking.transaction.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cobanking.services")
public record TransactionServiceProperties(ServiceEndpoint ledger, ServiceEndpoint account) {
    public record ServiceEndpoint(String baseUrl) {
    }
}
