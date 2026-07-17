package com.cobanking.customer.service;

import com.cobanking.common.exception.BusinessException;
import com.cobanking.common.exception.ResourceNotFoundException;
import com.cobanking.customer.domain.Customer;
import com.cobanking.customer.dto.CreateCustomerRequest;
import com.cobanking.customer.dto.CustomerResponse;
import com.cobanking.customer.repository.CustomerRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByTenantIdAndEmailIgnoreCase(request.tenantId(), request.email())) {
            throw new BusinessException("CUSTOMER_EMAIL_EXISTS", "A customer already exists with this email");
        }

        Customer customer = new Customer(
                request.tenantId(),
                request.fullName(),
                request.email(),
                request.phoneNumber());

        return toResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(UUID tenantId, UUID customerId) {
        Customer customer = customerRepository.findByTenantIdAndId(tenantId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("CUSTOMER_NOT_FOUND", "Customer was not found"));

        return toResponse(customer);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getTenantId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getStatus(),
                customer.getCreatedAt());
    }
}
