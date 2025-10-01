package com.kopo.hanabank.investment.domain;

import com.kopo.hanabank.common.domain.DateTimeEntity;
import com.kopo.hanabank.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investment_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvestmentAccount extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private InvestmentProduct product;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "investment_amount", nullable = false)
    private Long investmentAmount = 0L;

    @Column(name = "current_value", nullable = false)
    private Long currentValue = 0L;

    @Column(name = "profit_loss", nullable = false)
    private Long profitLoss = 0L;

    @Column(name = "profit_loss_rate", precision = 5, scale = 2)
    private BigDecimal profitLossRate = BigDecimal.ZERO;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder
    public InvestmentAccount(User user, InvestmentProduct product, String accountNumber, String accountName,
                           Long investmentAmount, LocalDate startDate) {
        this.user = user;
        this.product = product;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.investmentAmount = investmentAmount;
        this.currentValue = investmentAmount;
        this.profitLoss = 0L;
        this.profitLossRate = BigDecimal.ZERO;
        this.startDate = startDate;
        this.status = AccountStatus.ACTIVE;
    }

    public void invest(Long amount) {
        this.investmentAmount += amount;
        this.currentValue += amount;
    }

    public void redeem(Long amount) {
        if (this.currentValue < amount) {
            throw new IllegalArgumentException("투자금액이 부족합니다.");
        }
        this.currentValue -= amount;
    }

    public void updateCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
        this.profitLoss = this.currentValue - this.investmentAmount;
        if (this.investmentAmount > 0) {
            this.profitLossRate = BigDecimal.valueOf(this.profitLoss)
                    .divide(BigDecimal.valueOf(this.investmentAmount), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }

    public void close() {
        this.status = AccountStatus.CLOSED;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public String getAccountName() {
        return this.product.getProductName();
    }

    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public InvestmentProduct getProduct() {
        return this.product;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public Long getInvestmentAmount() {
        return this.investmentAmount;
    }

    public Long getCurrentValue() {
        return this.currentValue;
    }

    public Long getProfitLoss() {
        return this.profitLoss;
    }

    public BigDecimal getProfitLossRate() {
        return this.profitLossRate;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public AccountStatus getStatus() {
        return this.status;
    }

    public enum AccountStatus {
        ACTIVE("활성"),
        CLOSED("해지");

        private final String description;

        AccountStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}




