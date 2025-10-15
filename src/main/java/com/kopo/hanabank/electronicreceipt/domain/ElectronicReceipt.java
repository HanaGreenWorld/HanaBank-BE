package com.kopo.hanabank.electronicreceipt.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(
    name = "electronic_receipts",
    indexes = {
        @Index(name = "idx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_transaction_id", columnList = "transaction_id"),
        @Index(name = "idx_receipt_date", columnList = "receipt_date"),
        @Index(name = "idx_green_world_user", columnList = "is_green_world_user")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ElectronicReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "transaction_id", nullable = false, length = 100, unique = true)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "transaction_amount", nullable = false)
    private Long transactionAmount;

    @Column(name = "branch_name", length = 100)
    private String branchName;

    @Column(name = "receipt_date", nullable = false)
    private LocalDateTime receiptDate;

    @Column(name = "is_green_world_user", nullable = false)
    private Boolean isGreenWorldUser = false;

    @Column(name = "webhook_sent", nullable = false)
    private Boolean webhookSent = false;

    @Column(name = "webhook_sent_at")
    private LocalDateTime webhookSentAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.transactionId == null) {
            this.transactionId = generateTransactionId();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public ElectronicReceipt(Long customerId, String transactionId, TransactionType transactionType,
                           Long transactionAmount, String branchName, LocalDateTime receiptDate) {
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.branchName = branchName;
        this.receiptDate = receiptDate;
        this.isGreenWorldUser = false;
        this.webhookSent = false;
    }

    private String generateTransactionId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "ER" + dateTime + random;
    }

    public void setAsGreenWorldUser() {
        this.isGreenWorldUser = true;
    }

    public void markWebhookSent() {
        this.webhookSent = true;
        this.webhookSentAt = LocalDateTime.now();
    }
}