package com.kopo.hanabank.deposit.dto;

import com.kopo.hanabank.deposit.domain.DemandDepositAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandDepositAccountResponse {

    private Long id;
    private String accountNumber;
    private String accountName;
    private String bankCode;
    private String accountType;
    private String accountTypeDescription;
    private Long balance;
    private Long availableBalance;
    private LocalDate openDate;
    private LocalDate maturityDate;
    private BigDecimal baseInterestRate;
    private String status;
    private String statusDescription;
    private Boolean isActive;
    private LocalDateTime lastTransactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DemandDepositAccountResponse from(DemandDepositAccount account) {
        return DemandDepositAccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .bankCode(account.getBankCode())
                .accountType(account.getAccountType().name())
                .accountTypeDescription(account.getAccountType().getDescription())
                .balance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .openDate(account.getOpenDate())
                .maturityDate(account.getMaturityDate())
                .baseInterestRate(account.getBaseInterestRate())
                .status(account.getStatus().name())
                .statusDescription(account.getStatus().getDescription())
                .isActive(account.getIsActive())
                .lastTransactionDate(account.getLastTransactionDate())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getModifiedAt())
                .build();
    }
}
