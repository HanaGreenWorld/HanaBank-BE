package com.kopo.hanabank.loan.domain;

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
@Table(name = "loan_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanAccount extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct product;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "loan_amount", nullable = false)
    private Long loanAmount;

    @Column(name = "remaining_amount", nullable = false)
    private Long remainingAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "base_rate", precision = 5, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "preferential_rate", precision = 5, scale = 2)
    private BigDecimal preferentialRate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "monthly_payment", nullable = false)
    private Long monthlyPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status = LoanStatus.ACTIVE;

    @Builder
    public LoanAccount(User user, LoanProduct product, String accountNumber, String accountName,
                      Long loanAmount, BigDecimal interestRate,
                      LocalDate startDate, LocalDate maturityDate, Long monthlyPayment) {
        this.user = user;
        this.product = product;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.loanAmount = loanAmount;
        this.remainingAmount = loanAmount;
        this.interestRate = interestRate;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.monthlyPayment = monthlyPayment;
        this.status = LoanStatus.ACTIVE;
    }

    public void repay(Long amount) {
        if (this.remainingAmount < amount) {
            throw new IllegalArgumentException("상환금액이 잔여금액보다 큽니다.");
        }
        this.remainingAmount -= amount;
        
        if (this.remainingAmount <= 0) {
            this.status = LoanStatus.COMPLETED;
        }
    }

    public void suspend() {
        this.status = LoanStatus.SUSPENDED;
    }

    public void close() {
        this.status = LoanStatus.CLOSED;
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

    public LoanProduct getProduct() {
        return this.product;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public Long getLoanAmount() {
        return this.loanAmount;
    }

    public Long getRemainingAmount() {
        return this.remainingAmount;
    }

    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getMaturityDate() {
        return this.maturityDate;
    }

    public Long getMonthlyPayment() {
        return this.monthlyPayment;
    }

    public LoanStatus getStatus() {
        return this.status;
    }

    public enum LoanStatus {
        PENDING("심사중"),
        APPROVED("승인"),
        ACTIVE("활성"),
        SUSPENDED("정지"),
        COMPLETED("완료"),
        CLOSED("해지");

        private final String description;

        LoanStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}




