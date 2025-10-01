package com.kopo.hanabank.savings.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class SavingsAccountCreateRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @Positive(message = "우대금리는 양수여야 합니다.")
    private BigDecimal preferentialRate;

    @Positive(message = "가입금액은 양수여야 합니다.")
    private Long applicationAmount;

    private String customerInfoToken;
    
    // 자동이체 관련 필드들
    private Boolean autoTransferEnabled;
    private Integer transferDay;
    private Long monthlyTransferAmount;
    private String withdrawalAccountNumber;
    private String withdrawalBankName;
}









