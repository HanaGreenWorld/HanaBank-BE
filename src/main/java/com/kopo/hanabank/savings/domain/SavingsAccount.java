package com.kopo.hanabank.savings.domain;

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
@Table(name = "savings_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavingsAccount extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private SavingsProduct product;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "balance", nullable = false)
    private Long balance = 0L;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "base_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "preferential_rate", precision = 5, scale = 2)
    private BigDecimal preferentialRate;

    @Column(name = "final_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal finalRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 자동이체 관련 필드들
    @Column(name = "auto_transfer_enabled")
    private Boolean autoTransferEnabled = false;

    @Column(name = "transfer_day")
    private Integer transferDay; // 매월 이체할 날짜

    @Column(name = "monthly_transfer_amount")
    private Long monthlyTransferAmount; // 월 이체 금액

    @Column(name = "withdrawal_account_number")
    private String withdrawalAccountNumber; // 출금 계좌번호

    @Column(name = "withdrawal_bank_name")
    private String withdrawalBankName; // 출금 은행명

    @Builder
    public SavingsAccount(User user, SavingsProduct product, String accountNumber, String accountName,
                         Long balance, LocalDate startDate, LocalDate maturityDate,
                         BigDecimal baseRate, BigDecimal preferentialRate, BigDecimal finalRate,
                         Boolean autoTransferEnabled, Integer transferDay, Long monthlyTransferAmount,
                         String withdrawalAccountNumber, String withdrawalBankName) {
        this.user = user;
        this.product = product;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = balance != null ? balance : 0L;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.baseRate = baseRate;
        this.preferentialRate = preferentialRate;
        this.finalRate = finalRate;
        this.status = AccountStatus.ACTIVE;
        this.autoTransferEnabled = autoTransferEnabled != null ? autoTransferEnabled : false;
        this.transferDay = transferDay;
        this.monthlyTransferAmount = monthlyTransferAmount;
        this.withdrawalAccountNumber = withdrawalAccountNumber;
        this.withdrawalBankName = withdrawalBankName;
    }

    public void deposit(Long amount) {
        this.balance += amount;
    }

    public void withdraw(Long amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.balance -= amount;
    }

    public void close() {
        this.status = AccountStatus.CLOSED;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public void suspend() {
        this.status = AccountStatus.SUSPENDED;
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

    public SavingsProduct getProduct() {
        return this.product;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public Long getBalance() {
        return this.balance;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getMaturityDate() {
        return this.maturityDate;
    }

    public BigDecimal getBaseRate() {
        return this.baseRate;
    }

    public BigDecimal getPreferentialRate() {
        return this.preferentialRate;
    }

    public BigDecimal getFinalRate() {
        return this.finalRate;
    }

    public AccountStatus getStatus() {
        return this.status;
    }

    // 자동이체 설정 메서드
    public void setAutoTransfer(Boolean enabled, Integer day, Long amount, String accountNumber, String bankName) {
        this.autoTransferEnabled = enabled;
        this.transferDay = day;
        this.monthlyTransferAmount = amount;
        this.withdrawalAccountNumber = accountNumber;
        this.withdrawalBankName = bankName;
    }

    // 자동이체 해지 메서드
    public void disableAutoTransfer() {
        this.autoTransferEnabled = false;
        this.transferDay = null;
        this.monthlyTransferAmount = null;
        this.withdrawalAccountNumber = null;
        this.withdrawalBankName = null;
    }

    public enum AccountStatus {
        ACTIVE("활성"),
        SUSPENDED("정지"),
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