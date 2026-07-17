package com.cobanking.account.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate() {
        long number = 100_000_000_000L + Math.abs(RANDOM.nextLong() % 900_000_000_000L);
        return "CB" + number;
    }
}
