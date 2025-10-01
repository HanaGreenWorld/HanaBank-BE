package com.kopo.hanabank.deposit.dto;

import com.kopo.hanabank.deposit.domain.DemandDepositAccount;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandDepositAccountCreateRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "계좌 종류는 필수입니다.")
    private DemandDepositAccount.AccountType accountType;

    @Size(min = 1, max = 100, message = "계좌명은 1자 이상 100자 이하여야 합니다.")
    private String accountName;

    private LocalDate maturityDate;

    private BigDecimal baseInterestRate;

    public DemandDepositAccount toEntity(com.kopo.hanabank.user.domain.User user) {
        return DemandDepositAccount.builder()
                .user(user)
                .accountName(accountName)
                .accountType(accountType)
                .maturityDate(maturityDate)
                .baseInterestRate(baseInterestRate)
                .build();
    }
}
