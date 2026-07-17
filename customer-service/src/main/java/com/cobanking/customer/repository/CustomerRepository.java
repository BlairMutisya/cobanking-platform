package com.cobanking.customer.repository;

import com.cobanking.customer.domain.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByTenantIdAndEmailIgnoreCase(UUID tenantId, String email);

    Optional<Customer> findByTenantIdAndId(UUID tenantId, UUID id);
}
