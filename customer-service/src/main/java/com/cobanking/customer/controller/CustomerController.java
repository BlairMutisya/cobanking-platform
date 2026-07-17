package com.cobanking.customer.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import com.cobanking.customer.dto.CreateCustomerRequest;
import com.cobanking.customer.dto.CustomerResponse;
import com.cobanking.customer.service.CustomerService;
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
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Customer service is ready",
                new ServiceInfo("customer-service", "UP", "Customer profile foundation"));
    }

    @PostMapping
    public ApiResponse<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return ApiResponse.ok("Customer created", customerService.createCustomer(request));
    }

    @GetMapping("/{customerId}")
    public ApiResponse<CustomerResponse> getCustomer(
            @PathVariable UUID customerId,
            @RequestParam UUID tenantId) {
        return ApiResponse.ok("Customer found", customerService.getCustomer(tenantId, customerId));
    }
}
