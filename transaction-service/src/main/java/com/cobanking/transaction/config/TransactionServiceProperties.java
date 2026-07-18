package com.cobanking.transaction.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cobanking.services.ledger")
public record TransactionServiceProperties(String baseUrl) {
}
