package com.cobanking.customer.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import com.cobanking.customer.dto.CreateCustomerRequest;
import com.cobanking.customer.dto.CustomerResponse;
import com.cobanking.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Customer profile management")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/health")
    @Operation(summary = "Check customer service health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Customer service is ready",
                new ServiceInfo("customer-service", "UP", "Customer profile foundation"));
    }

    @PostMapping
    @Operation(summary = "Create a customer", description = "Creates a customer under a tenant using UUID identifiers")
    public ApiResponse<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return ApiResponse.ok("Customer created", customerService.createCustomer(request));
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get a customer by UUID")
    public ApiResponse<CustomerResponse> getCustomer(
            @PathVariable UUID customerId,
            @RequestParam UUID tenantId) {
        return ApiResponse.ok("Customer found", customerService.getCustomer(tenantId, customerId));
    }
}
