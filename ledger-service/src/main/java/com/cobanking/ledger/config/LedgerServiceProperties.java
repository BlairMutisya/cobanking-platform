package com.cobanking.ledger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cobanking.services.audit")
public record LedgerServiceProperties(String baseUrl) {
}
