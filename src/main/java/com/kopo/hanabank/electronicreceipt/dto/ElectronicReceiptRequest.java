package com.kopo.hanabank.electronicreceipt.dto;

import com.kopo.hanabank.electronicreceipt.domain.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ElectronicReceiptRequest {
    
    private Long userId;
    private String transactionId;
    private TransactionType transactionType;
    private Long transactionAmount;
    private String branchName;
    private LocalDateTime receiptDate;

    @Builder
    public ElectronicReceiptRequest(Long userId, String transactionId, TransactionType transactionType,
                                   Long transactionAmount, String branchName, LocalDateTime receiptDate) {
        this.userId = userId;
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.branchName = branchName;
        this.receiptDate = receiptDate;
    }
}