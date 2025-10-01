package com.kopo.hanabank.savings.dto;

import com.kopo.hanabank.savings.domain.SavingsProduct;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class SavingsProductResponse {
    private Long productId;
    private String productName;
    private String productType;
    private String description;
    private BigDecimal basicRate;
    private BigDecimal maxRate;
    private Long minAmount;
    private Long maxAmount;
    private Integer termMonths;
    private String features;
    private String benefits;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public SavingsProductResponse(SavingsProduct savingsProduct) {
        this.productId = savingsProduct.getProductId();
        this.productName = savingsProduct.getProductName();
        this.productType = savingsProduct.getProductType();
        this.description = savingsProduct.getDescription();
        this.basicRate = savingsProduct.getBasicRate();
        this.maxRate = savingsProduct.getMaxRate();
        this.minAmount = savingsProduct.getMinAmount();
        this.maxAmount = savingsProduct.getMaxAmount();
        this.termMonths = savingsProduct.getTermMonths();
        this.features = savingsProduct.getFeatures();
        this.benefits = savingsProduct.getBenefits();
        this.imageUrl = savingsProduct.getImageUrl();
        this.isActive = savingsProduct.getIsActive();
        this.createdAt = savingsProduct.getCreatedAt();
        this.modifiedAt = savingsProduct.getModifiedAt();
    }
}
