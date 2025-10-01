package com.kopo.hanabank.savings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SavingsTransactionRequest {

    @NotBlank(message = "계좌번호는 필수입니다.")
    private String accountNumber;

    @NotNull(message = "금액은 필수입니다.")
    @Positive(message = "금액은 양수여야 합니다.")
    private Long amount;
}












