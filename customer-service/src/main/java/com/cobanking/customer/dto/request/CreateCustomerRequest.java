package com.cobanking.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateCustomerRequest(
        @NotNull UUID tenantId,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String phoneNumber
) {
}
