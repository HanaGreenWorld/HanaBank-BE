package com.kopo.hanabank.electronicreceipt.dto;

import com.kopo.hanabank.electronicreceipt.domain.ElectronicReceipt;
import com.kopo.hanabank.electronicreceipt.domain.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 전자영수증 응답 DTO
 */
@Getter
@NoArgsConstructor
public class ElectronicReceiptResponse {
    
    private Long receiptId;
    private Long customerId;
    private String transactionId;
    private TransactionType transactionType;
    private Long transactionAmount;
    private String branchName;
    private LocalDateTime receiptDate;
    private Boolean isGreenWorldUser;
    private Boolean webhookSent;
    private LocalDateTime webhookSentAt;
    private LocalDateTime createdAt;

    @Builder
    public ElectronicReceiptResponse(Long receiptId, Long customerId, String transactionId,
                                   TransactionType transactionType, Long transactionAmount, String branchName,
                                   LocalDateTime receiptDate, Boolean isGreenWorldUser, Boolean webhookSent,
                                   LocalDateTime webhookSentAt, LocalDateTime createdAt) {
        this.receiptId = receiptId;
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.branchName = branchName;
        this.receiptDate = receiptDate;
        this.isGreenWorldUser = isGreenWorldUser;
        this.webhookSent = webhookSent;
        this.webhookSentAt = webhookSentAt;
        this.createdAt = createdAt;
    }

    /**
     * ElectronicReceipt 엔티티로부터 응답 DTO 생성
     */
    public static ElectronicReceiptResponse from(ElectronicReceipt receipt) {
        return ElectronicReceiptResponse.builder()
            .receiptId(receipt.getReceiptId())
            .customerId(receipt.getCustomerId())
            .transactionId(receipt.getTransactionId())
            .transactionType(receipt.getTransactionType())
            .transactionAmount(receipt.getTransactionAmount())
            .branchName(receipt.getBranchName())
            .receiptDate(receipt.getReceiptDate())
            .isGreenWorldUser(receipt.getIsGreenWorldUser())
            .webhookSent(receipt.getWebhookSent())
            .webhookSentAt(receipt.getWebhookSentAt())
            .createdAt(receipt.getCreatedAt())
            .build();
    }
}