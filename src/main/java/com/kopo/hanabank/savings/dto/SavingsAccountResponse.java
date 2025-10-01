package com.kopo.hanabank.savings.dto;

import com.kopo.hanabank.savings.domain.SavingsAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;
    private String accountNumber;
    private Long balance;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private BigDecimal baseRate;
    private BigDecimal preferentialRate;
    private BigDecimal finalRate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SavingsAccountResponse(SavingsAccount account) {
        this.id = account.getId();
        this.userId = account.getUser().getId();
        this.userName = account.getUser().getName();
        this.productId = account.getProduct().getId();
        this.productName = account.getProduct().getName();
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.startDate = account.getStartDate();
        this.maturityDate = account.getMaturityDate();
        this.baseRate = account.getBaseRate();
        this.preferentialRate = account.getPreferentialRate();
        this.finalRate = account.getFinalRate();
        this.status = account.getStatus().getDescription();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getModifiedAt();
    }
}

