package com.kopo.hanabank.investment.domain;

import com.kopo.hanabank.common.domain.DateTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "investment_products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvestmentProduct extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @Column(name = "expected_return_rate", precision = 5, scale = 2)
    private BigDecimal expectedReturnRate;

    @Column(name = "risk_level", length = 20)
    private Integer riskLevel; // 낮음, 보통, 높음

    @Column(name = "min_investment_amount", nullable = false)
    private Long minInvestmentAmount;

    @Column(name = "max_investment_amount")
    private Long maxInvestmentAmount;

    @Column(name = "management_fee", precision = 5, scale = 2)
    private BigDecimal managementFee;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder
    public InvestmentProduct(Long id, String name, String description, ProductType productType,
                           BigDecimal expectedReturnRate, Integer riskLevel,
                           Long minInvestmentAmount, Long maxInvestmentAmount,
                           BigDecimal managementFee, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.productType = productType;
        this.expectedReturnRate = expectedReturnRate;
        this.riskLevel = riskLevel;
        this.minInvestmentAmount = minInvestmentAmount;
        this.maxInvestmentAmount = maxInvestmentAmount;
        this.managementFee = managementFee;
        this.isActive = isActive != null ? isActive : true;
    }

    public String getProductName() {
        return this.name;
    }

    public Long getProductId() {
        return this.id;
    }

    public String getProductType() {
        return this.productType.name();
    }

    public String getDescription() {
        return this.description;
    }

    public Long getMinInvestmentAmount() {
        return this.minInvestmentAmount;
    }

    public Long getMaxInvestmentAmount() {
        return this.maxInvestmentAmount;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public enum ProductType {
        STOCK_FUND("주식형펀드"),
        BOND_FUND("채권형펀드"),
        MIXED_FUND("혼합형펀드"),
        MONEY_MARKET_FUND("머니마켓펀드"),
        ETF("ETF"),
        REIT("리츠");

        private final String description;

        ProductType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}




