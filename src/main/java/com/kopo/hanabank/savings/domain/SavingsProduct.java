package com.kopo.hanabank.savings.domain;

import com.kopo.hanabank.common.domain.DateTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "savings_products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavingsProduct extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Getter
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_type", nullable = false, length = 50)
    private String productType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "basic_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal basicRate;

    @Column(name = "preferential_rate", precision = 5, scale = 2)
    private BigDecimal preferentialRate;

    @Column(name = "max_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxRate;

    @Column(name = "min_amount", nullable = false)
    private Long minAmount;

    @Column(name = "max_amount", nullable = false)
    private Long maxAmount;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "deposit_type", nullable = false)
    private DepositType depositType;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features;

    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "interest_payment_type", length = 50)
    private String interestPaymentType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder
    public SavingsProduct(String productName, String productType, String description,
                         BigDecimal basicRate, BigDecimal preferentialRate, BigDecimal maxRate, Long minAmount, Long maxAmount,
                         Integer termMonths, DepositType depositType, String features, String benefits, String imageUrl,
                         String interestPaymentType, Boolean isActive) {
        this.productName = productName;
        this.productType = productType;
        this.description = description;
        this.basicRate = basicRate;
        this.preferentialRate = preferentialRate;
        this.maxRate = maxRate;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.termMonths = termMonths;
        this.depositType = depositType;
        this.features = features;
        this.benefits = benefits;
        this.imageUrl = imageUrl;
        this.interestPaymentType = interestPaymentType;
        this.isActive = isActive != null ? isActive : true;
    }

    public void updateProduct(String productName, String productType, String description,
                             BigDecimal basicRate, BigDecimal maxRate, Long minAmount, Long maxAmount,
                             Integer termMonths, DepositType depositType, String features, String benefits, String imageUrl) {
        this.productName = productName;
        this.productType = productType;
        this.description = description;
        this.basicRate = basicRate;
        this.maxRate = maxRate;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.termMonths = termMonths;
        this.depositType = depositType;
        this.features = features;
        this.benefits = benefits;
        this.imageUrl = imageUrl;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    @Builder
    public SavingsProduct(Long productId, String productName, String productType, String description,
                         BigDecimal basicRate, BigDecimal maxRate, Long minAmount, Long maxAmount,
                         Integer termMonths, DepositType depositType, String features, String benefits, String imageUrl, Boolean isActive) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.description = description;
        this.basicRate = basicRate;
        this.maxRate = maxRate;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.termMonths = termMonths;
        this.depositType = depositType;
        this.features = features;
        this.benefits = benefits;
        this.imageUrl = imageUrl;
        this.isActive = isActive != null ? isActive : true;
    }

    // Additional getter methods for DTOs
    public Long getId() {
        return this.productId;
    }

    public String getName() {
        return this.productName;
    }

    public BigDecimal getBaseRate() {
        return this.basicRate;
    }

    public Integer getPeriodMonths() {
        return this.termMonths;
    }

    public enum DepositType {
        REGULAR_SAVINGS("정기예금"),
        FIXED_DEPOSIT("정기적금"),
        FREE_SAVINGS("자유적금"),
        INSTALLMENT_SAVINGS("정기적금"),
        HIGH_YIELD_SAVINGS("고금리예금"),
        FOREIGN_CURRENCY_DEPOSIT("외화예금"),
        INTEREST_FREE_SAVINGS("무이자적금"),
        SPECIAL_SAVINGS("특별적금"),
        PENSION_SAVINGS("연금저축"),
        HOUSING_SAVINGS("주택청약저축");

        private final String description;

        DepositType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}