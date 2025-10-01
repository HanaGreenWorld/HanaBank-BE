package com.kopo.hanabank.deposit.domain;

import com.kopo.hanabank.common.domain.DateTimeEntity;
import com.kopo.hanabank.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demand_deposit_accounts")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DemandDepositAccount extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "bank_code", nullable = false)
    @Builder.Default
    private String bankCode = "081"; // 하나은행 코드

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "balance", nullable = false)
    @Builder.Default
    private Long balance = 0L;

    @Column(name = "available_balance", nullable = false)
    @Builder.Default
    private Long availableBalance = 0L;

    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "base_interest_rate", precision = 5, scale = 2)
    private BigDecimal baseInterestRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_transaction_date")
    private LocalDateTime lastTransactionDate;

    public DemandDepositAccount(User user, String accountNumber, String accountName,
                               AccountType accountType, LocalDate openDate, LocalDate maturityDate,
                               BigDecimal baseInterestRate) {
        this.user = user;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.accountType = accountType;
        this.openDate = openDate;
        this.maturityDate = maturityDate;
        this.baseInterestRate = baseInterestRate;
        this.balance = 0L;
        this.availableBalance = 0L;
        this.status = AccountStatus.ACTIVE;
    }

    // 입금
    public void deposit(Long amount) {
        this.balance += amount;
        this.availableBalance += amount;
        this.lastTransactionDate = LocalDateTime.now();
    }

    // 출금
    public void withdraw(Long amount) {
        if (this.availableBalance < amount) {
            throw new IllegalArgumentException("사용 가능한 잔액이 부족합니다.");
        }
        this.balance -= amount;
        this.availableBalance -= amount;
        this.lastTransactionDate = LocalDateTime.now();
    }

    // 계좌 해지
    public void close() {
        this.status = AccountStatus.CLOSED;
        this.isActive = false;
    }

    // 계좌 정지
    public void suspend() {
        this.status = AccountStatus.SUSPENDED;
    }

    // 계좌 활성화
    public void activate() {
        this.status = AccountStatus.ACTIVE;
        this.isActive = true;
    }

    // 잔액 업데이트 (관리자 기능)
    public void updateBalance(Long newBalance) {
        this.balance = newBalance;
        this.availableBalance = newBalance;
        this.lastTransactionDate = LocalDateTime.now();
    }

    public enum AccountType {
        CHECKING("입출금예금"),
        SAVINGS("저축예금"),
        TIME_DEPOSIT("정기예금");

        private final String description;

        AccountType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum AccountStatus {
        ACTIVE("활성"),
        SUSPENDED("정지"),
        CLOSED("해지"),
        DORMANT("휴면");

        private final String description;

        AccountStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
