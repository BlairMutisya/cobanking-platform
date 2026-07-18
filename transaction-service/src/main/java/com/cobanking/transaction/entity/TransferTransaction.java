package com.cobanking.transaction.entity;

import com.cobanking.transaction.enums.TransactionStatus;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfer_transactions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_transaction_idempotency", columnNames = {"tenant_id", "idempotency_key"})
})
public class TransferTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "idempotency_key", nullable = false, length = 120)
    private String idempotencyKey;

    @Column(name = "debit_account_id", nullable = false)
    private UUID debitAccountId;

    @Column(name = "credit_account_id", nullable = false)
    private UUID creditAccountId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionStatus status;

    @Column(name = "ledger_batch_id")
    private UUID ledgerBatchId;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected TransferTransaction() {
    }

    public TransferTransaction(
            UUID tenantId,
            String idempotencyKey,
            UUID debitAccountId,
            UUID creditAccountId,
            BigDecimal amount,
            String currency) {
        this.tenantId = tenantId;
        this.idempotencyKey = idempotencyKey;
        this.debitAccountId = debitAccountId;
        this.creditAccountId = creditAccountId;
        this.amount = amount;
        this.currency = currency.toUpperCase();
        this.status = TransactionStatus.RECEIVED;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public void markPosted(UUID ledgerBatchId) {
        this.status = TransactionStatus.POSTED;
        this.ledgerBatchId = ledgerBatchId;
        this.failureReason = null;
    }

    public void markFailed(String failureReason) {
        this.status = TransactionStatus.FAILED;
        this.failureReason = failureReason;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public UUID getDebitAccountId() {
        return debitAccountId;
    }

    public UUID getCreditAccountId() {
        return creditAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public UUID getLedgerBatchId() {
        return ledgerBatchId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
