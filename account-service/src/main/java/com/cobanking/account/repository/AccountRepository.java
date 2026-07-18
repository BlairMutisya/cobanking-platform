package com.cobanking.account.repository;

import com.cobanking.account.entity.Account;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByTenantIdAndId(UUID tenantId, UUID id);
}
