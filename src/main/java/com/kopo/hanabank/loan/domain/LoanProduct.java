package com.kopo.hanabank.loan.domain;

import com.kopo.hanabank.common.domain.DateTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanProduct extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_type", nullable = false, length = 50)
    private String productType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "preferential_rate", precision = 5, scale = 2)
    private BigDecimal preferentialRate;

    @Column(name = "max_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxRate;

    @Column(name = "max_amount", nullable = false)
    private Long maxAmount;

    @Column(name = "min_amount", nullable = false)
    private Long minAmount;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "min_period_months", nullable = false)
    private Integer minPeriodMonths;

    @Column(name = "max_period_months", nullable = false)
    private Integer maxPeriodMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features;

    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder
    public LoanProduct(String productName, String productType, String description,
                      BigDecimal baseRate, BigDecimal preferentialRate, BigDecimal maxRate, Long maxAmount, Long minAmount,
                      Integer periodMonths, LoanType loanType, String features, String benefits, String requirements, String imageUrl, Boolean isActive) {
        this.productName = productName;
        this.productType = productType;
        this.description = description;
        this.baseRate = baseRate;
        this.preferentialRate = preferentialRate;
        this.maxRate = maxRate;
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.termMonths = periodMonths;
        this.minPeriodMonths = periodMonths;
        this.maxPeriodMonths = periodMonths;
        this.loanType = loanType;
        this.features = features;
        this.benefits = benefits;
        this.requirements = requirements;
        this.imageUrl = imageUrl;
        this.isActive = isActive != null ? isActive : true;
    }

    public void updateProduct(String productName, String productType, String description,
                             BigDecimal baseRate, BigDecimal preferentialRate, BigDecimal maxRate, Long maxAmount, Long minAmount,
                             Integer termMonths, Integer minPeriodMonths, Integer maxPeriodMonths,
                             LoanType loanType, String features, String benefits, String requirements, String imageUrl) {
        this.productName = productName;
        this.productType = productType;
        this.description = description;
        this.baseRate = baseRate;
        this.preferentialRate = preferentialRate;
        this.maxRate = maxRate;
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.termMonths = termMonths;
        this.minPeriodMonths = minPeriodMonths;
        this.maxPeriodMonths = maxPeriodMonths;
        this.loanType = loanType;
        this.features = features;
        this.benefits = benefits;
        this.requirements = requirements;
        this.imageUrl = imageUrl;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public BigDecimal getInterestRate() {
        return this.baseRate;
    }

    public Long getProductId() {
        return this.productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public String getProductType() {
        return this.productType;
    }

    public String getDescription() {
        return this.description;
    }

    public Long getMaxAmount() {
        return this.maxAmount;
    }

    public Long getMinAmount() {
        return this.minAmount;
    }

    public Integer getTermMonths() {
        return this.termMonths;
    }

    public Integer getMinPeriodMonths() {
        return this.minPeriodMonths;
    }

    public Integer getMaxPeriodMonths() {
        return this.maxPeriodMonths;
    }

    public String getFeatures() {
        return this.features;
    }

    public String getBenefits() {
        return this.benefits;
    }

    public String getRequirements() {
        return this.requirements;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    @Builder
    public LoanProduct(Long productId, String productName, String productType, String description,
                      BigDecimal baseRate, BigDecimal preferentialRate, BigDecimal maxRate, Long maxAmount, Long minAmount,
                      Integer termMonths, Integer minPeriodMonths, Integer maxPeriodMonths,
                      LoanType loanType, String features, String benefits, String requirements, String imageUrl, Boolean isActive) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.description = description;
        this.baseRate = baseRate;
        this.preferentialRate = preferentialRate;
        this.maxRate = maxRate;
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.termMonths = termMonths;
        this.minPeriodMonths = minPeriodMonths;
        this.maxPeriodMonths = maxPeriodMonths;
        this.loanType = loanType;
        this.features = features;
        this.benefits = benefits;
        this.requirements = requirements;
        this.imageUrl = imageUrl;
        this.isActive = isActive != null ? isActive : true;
    }

    public enum LoanType {
        PERSONAL_LOAN("개인신용대출"),
        MORTGAGE("주택담보대출"),
        BUSINESS_LOAN("사업자대출"),
        STUDENT_LOAN("학자금대출"),
        CAR_LOAN("자동차대출"),
        CREDIT_LOAN("신용대출"),
        GUARANTEE_LOAN("보증대출"),
        SPECIAL_LOAN("특별대출");

        private final String description;

        LoanType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}