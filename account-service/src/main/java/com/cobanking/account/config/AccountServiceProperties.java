package com.cobanking.account.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cobanking.services.audit")
public record AccountServiceProperties(String baseUrl) {
}
