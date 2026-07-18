package com.cobanking.customer.service;

import com.cobanking.customer.dto.request.CreateCustomerRequest;
import com.cobanking.customer.dto.response.CustomerResponse;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse getCustomer(UUID tenantId, UUID customerId);
}
