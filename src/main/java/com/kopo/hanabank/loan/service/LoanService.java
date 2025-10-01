package com.kopo.hanabank.loan.service;

import com.kopo.hanabank.common.exception.BusinessException;
import com.kopo.hanabank.common.exception.ErrorCode;
import com.kopo.hanabank.loan.domain.LoanAccount;
import com.kopo.hanabank.loan.domain.LoanProduct;
import com.kopo.hanabank.loan.repository.LoanAccountRepository;
import com.kopo.hanabank.loan.repository.LoanProductRepository;
import com.kopo.hanabank.user.domain.User;
import com.kopo.hanabank.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    private final LoanProductRepository loanProductRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final UserService userService;

    public List<LoanProduct> getAllLoanProducts() {
        return loanProductRepository.findByIsActiveTrue();
    }

    public List<LoanProduct> getLoanProductsByType(LoanProduct.LoanType loanType) {
        return loanProductRepository.findByIsActiveTrueAndLoanType(loanType);
    }

    public List<LoanProduct> getLoanProductsByAmount(Long amount) {
        return loanProductRepository.findByIsActiveTrueAndMinAmountLessThanEqualAndMaxAmountGreaterThanEqual(amount, amount);
    }

    public LoanProduct getLoanProductById(Long id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_PRODUCT_NOT_FOUND));
    }

    @Transactional
    public LoanAccount createLoanAccount(Long userId, Long productId, Long loanAmount, 
                                       Integer periodMonths) {
        User user = userService.getUserById(userId);
        LoanProduct product = getLoanProductById(productId);

        // 대출 금액 검증
        if (loanAmount < product.getMinAmount() || loanAmount > product.getMaxAmount()) {
            throw new BusinessException(ErrorCode.INVALID_LOAN_AMOUNT);
        }

        // 대출 기간 검증
        if (periodMonths < product.getMinPeriodMonths() || periodMonths > product.getMaxPeriodMonths()) {
            throw new BusinessException(ErrorCode.INVALID_LOAN_AMOUNT);
        }

        // 계좌번호 생성
        String accountNumber = generateAccountNumber();

        // 중복 체크
        if (loanAccountRepository.existsByAccountNumber(accountNumber)) {
            throw new BusinessException(ErrorCode.LOAN_APPLICATION_REJECTED);
        }

        // 월 상환액 계산 (원리금균등상환)
        BigDecimal monthlyRate = product.getInterestRate().divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_UP)
                .divide(BigDecimal.valueOf(12), 6, BigDecimal.ROUND_HALF_UP);
        
        BigDecimal monthlyPayment = calculateMonthlyPayment(loanAmount, monthlyRate, periodMonths);

        // 만기일 계산
        LocalDate startDate = LocalDate.now();
        LocalDate maturityDate = startDate.plusMonths(periodMonths);

        LoanAccount account = LoanAccount.builder()
                .user(user)
                .product(product)
                .accountNumber(accountNumber)
                .loanAmount(loanAmount)
                .interestRate(product.getInterestRate())
                .startDate(startDate)
                .maturityDate(maturityDate)
                .monthlyPayment(monthlyPayment.longValue())
                .build();

        return loanAccountRepository.save(account);
    }

    public List<LoanAccount> getUserLoanAccounts(Long userId) {
        User user = userService.getUserById(userId);
        return loanAccountRepository.findByUser(user);
    }

    public LoanAccount getLoanAccountByNumber(String accountNumber) {
        return loanAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_ACCOUNT_NOT_FOUND));
    }

    @Transactional
    public LoanAccount repayLoan(String accountNumber, Long amount) {
        LoanAccount account = getLoanAccountByNumber(accountNumber);
        
        if (account.getStatus() != LoanAccount.LoanStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.INVALID_LOAN_AMOUNT);
        }

        account.repay(amount);
        return account;
    }

    @Transactional
    public void suspendLoan(String accountNumber) {
        LoanAccount account = getLoanAccountByNumber(accountNumber);
        account.suspend();
    }

    @Transactional
    public void closeLoanAccount(String accountNumber) {
        LoanAccount account = getLoanAccountByNumber(accountNumber);
        account.close();
    }

    private String generateAccountNumber() {
        return "LOAN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private BigDecimal calculateMonthlyPayment(Long principal, BigDecimal monthlyRate, Integer months) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(principal).divide(BigDecimal.valueOf(months), 0, BigDecimal.ROUND_UP);
        }
        
        BigDecimal rate = monthlyRate.add(BigDecimal.ONE);
        BigDecimal power = rate.pow(months);
        BigDecimal numerator = BigDecimal.valueOf(principal).multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);
        
        return numerator.divide(denominator, 0, BigDecimal.ROUND_UP);
    }
}












