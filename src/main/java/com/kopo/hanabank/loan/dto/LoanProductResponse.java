package com.kopo.hanabank.loan.dto;

import com.kopo.hanabank.loan.domain.LoanProduct;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class LoanProductResponse {
    private Long productId;
    private String productName;
    private String productType;
    private String description;
    private BigDecimal interestRate;
    private Long maxAmount;
    private Long minAmount;
    private Integer termMonths;
    private String features;
    private String benefits;
    private String requirements;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public LoanProductResponse(LoanProduct loanProduct) {
        this.productId = loanProduct.getProductId();
        this.productName = loanProduct.getProductName();
        this.productType = loanProduct.getProductType();
        this.description = loanProduct.getDescription();
        this.interestRate = loanProduct.getInterestRate();
        this.maxAmount = loanProduct.getMaxAmount();
        this.minAmount = loanProduct.getMinAmount();
        this.termMonths = loanProduct.getTermMonths();
        this.features = loanProduct.getFeatures();
        this.benefits = loanProduct.getBenefits();
        this.requirements = loanProduct.getRequirements();
        this.imageUrl = loanProduct.getImageUrl();
        this.isActive = loanProduct.getIsActive();
        this.createdAt = loanProduct.getCreatedAt();
        this.modifiedAt = loanProduct.getModifiedAt();
    }
}
