package com.cobanking.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_tenant_email", columnNames = {"tenant_id", "email"})
})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(name = "phone_number", length = 40)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CustomerStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Customer() {
    }

    public Customer(UUID tenantId, String fullName, String email, String phoneNumber) {
        this.tenantId = tenantId;
        this.fullName = fullName;
        this.email = email.toLowerCase();
        this.phoneNumber = phoneNumber;
        this.status = CustomerStatus.ACTIVE;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
