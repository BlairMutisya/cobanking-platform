package com.cobanking.customer.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @GetMapping("/health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Customer service is ready",
                new ServiceInfo("customer-service", "UP", "Customer profile foundation"));
    }

    @PostMapping
    public ApiResponse<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return ApiResponse.ok("Customer creation placeholder accepted",
                new CustomerResponse("customer-id-will-be-generated-later", request.fullName(), request.email()));
    }

    public record CreateCustomerRequest(@NotBlank String fullName, @NotBlank @Email String email) {
    }

    public record CustomerResponse(String customerId, String fullName, String email) {
    }
}
