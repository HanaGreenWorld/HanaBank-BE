package com.kopo.hanabank.loan.dto;

import com.kopo.hanabank.loan.domain.LoanAccount;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class LoanAccountResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;
    private String accountNumber;
    private String accountName;
    private Long loanAmount;
    private Long remainingAmount;
    private BigDecimal interestRate;
    private BigDecimal baseRate;
    private BigDecimal preferentialRate;
    private Long monthlyPayment;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LoanAccountResponse(LoanAccount account) {
        this.id = account.getId();
        this.userId = account.getUser().getId();
        this.userName = account.getUser().getName();
        this.productId = account.getProduct().getProductId();
        this.productName = account.getProduct().getProductName();
        this.accountNumber = account.getAccountNumber();
        this.accountName = account.getAccountName();
        this.loanAmount = account.getLoanAmount();
        this.remainingAmount = account.getRemainingAmount();
        this.interestRate = account.getInterestRate();
        this.baseRate = account.getBaseRate();
        this.preferentialRate = account.getPreferentialRate();
        this.monthlyPayment = account.getMonthlyPayment();
        this.startDate = account.getStartDate();
        this.maturityDate = account.getMaturityDate();
        this.status = account.getStatus().getDescription();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getModifiedAt();
    }
}
