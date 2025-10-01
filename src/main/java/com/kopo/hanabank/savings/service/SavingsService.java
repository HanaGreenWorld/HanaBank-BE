package com.kopo.hanabank.savings.service;

import com.kopo.hanabank.common.exception.BusinessException;
import com.kopo.hanabank.common.exception.ErrorCode;
import com.kopo.hanabank.deposit.service.DemandDepositAccountService;
import com.kopo.hanabank.savings.domain.SavingsAccount;
import com.kopo.hanabank.savings.domain.SavingsProduct;
import com.kopo.hanabank.savings.repository.SavingsAccountRepository;
import com.kopo.hanabank.savings.repository.SavingsProductRepository;
import com.kopo.hanabank.user.domain.User;
import com.kopo.hanabank.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SavingsService {

    private final SavingsProductRepository savingsProductRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final UserService userService;
    private final DemandDepositAccountService demandDepositAccountService;

    public List<SavingsProduct> getAllSavingsProducts() {
        return savingsProductRepository.findByIsActiveTrue();
    }

    public List<SavingsProduct> getSavingsProductsByType(SavingsProduct.DepositType depositType) {
        return savingsProductRepository.findByIsActiveTrueAndDepositType(depositType);
    }

    public SavingsProduct getSavingsProductById(Long productId) {
        return savingsProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVINGS_PRODUCT_NOT_FOUND));
    }

    /**
     * 적금 계좌 생성 (기본 메서드)
     */
    @Transactional
    public SavingsAccount createSavingsAccount(Long userId, Long productId,
                                             BigDecimal preferentialRate, Long applicationAmount) {
        return createSavingsAccountWithAutoTransfer(userId, productId, preferentialRate, applicationAmount,
                false, null, null, null, null);
    }

    /**
     * 자동이체 설정을 포함한 적금 계좌 생성 (기본 메서드)
     * 실제 출금/입금 처리를 포함한 트랜잭션 처리
     */
    @Transactional
    public SavingsAccount createSavingsAccountWithAutoTransfer(Long userId, Long productId,
                                                             BigDecimal preferentialRate, Long applicationAmount,
                                                             Boolean autoTransferEnabled, Integer transferDay,
                                                             Long monthlyTransferAmount, String withdrawalAccountNumber,
                                                             String withdrawalBankName) {
        log.info("적금 계좌 생성 시작 - 사용자ID: {}, 상품ID: {}, 가입금액: {}", userId, productId, applicationAmount);
        
        User user = userService.getUserById(userId);
        SavingsProduct product = getSavingsProductById(productId);

        // 계좌번호 생성
        String accountNumber = generateAccountNumber();

        // 중복 체크
        if (savingsAccountRepository.existsByAccountNumber(accountNumber)) {
            throw new BusinessException(ErrorCode.SAVINGS_ACCOUNT_ALREADY_EXISTS);
        }

        // 이자율 계산
        BigDecimal finalRate = preferentialRate != null ? 
                product.getBaseRate().add(preferentialRate) : product.getBaseRate();

        // 만기일 계산
        LocalDate startDate = LocalDate.now();
        LocalDate maturityDate = startDate.plusMonths(product.getPeriodMonths());

        // 적금 계좌 생성 (초기 잔고는 0)
        SavingsAccount account = SavingsAccount.builder()
                .user(user)
                .product(product)
                .accountNumber(accountNumber)
                .accountName("하나green세상 적금")
                .balance(0L) // 초기 잔고는 0으로 시작 (Long 타입)
                .startDate(startDate)
                .maturityDate(maturityDate)
                .baseRate(product.getBaseRate())
                .preferentialRate(preferentialRate)
                .finalRate(finalRate)
                .autoTransferEnabled(autoTransferEnabled)
                .transferDay(transferDay)
                .monthlyTransferAmount(monthlyTransferAmount)
                .withdrawalAccountNumber(withdrawalAccountNumber)
                .withdrawalBankName(withdrawalBankName)
                .build();

        // 적금 계좌 저장
        SavingsAccount savedAccount = savingsAccountRepository.save(account);
        log.info("적금 계좌 생성 완료 - 계좌번호: {}", accountNumber);

        // 실제 출금/입금 처리
        if (applicationAmount > 0) { // Long 타입으로 비교
            try {
                log.info("💰 실제 출금/입금 처리 시작 - 금액: {}원", applicationAmount);
                
                // 1. 출금 계좌에서 출금 (withdrawalAccountNumber가 있는 경우)
                if (withdrawalAccountNumber != null && !withdrawalAccountNumber.isEmpty()) {
                    log.info("💸 출금 계좌에서 출금 처리: {} -> {}원", withdrawalAccountNumber, applicationAmount);
                    demandDepositAccountService.withdraw(withdrawalAccountNumber, applicationAmount);
                    log.info("✅ 출금 완료 - 계좌: {}, 금액: {}원", withdrawalAccountNumber, applicationAmount);
                } else {
                    log.warn("⚠️ 출금 계좌 정보가 없어 출금 처리를 건너뜁니다.");
                }
                
                // 2. 적금 계좌에 입금
                log.info("💳 적금 계좌에 입금 처리: {} -> {}원", accountNumber, applicationAmount);
                savedAccount = depositToSavings(accountNumber, applicationAmount);
                
                log.info("🎉 적금 가입 완료 - 계좌: {}, 최종 잔고: {}원", accountNumber, savedAccount.getBalance());
                
            } catch (Exception e) {
                log.error("❌ 적금 가입 중 출금/입금 처리 실패: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.SAVINGS_ACCOUNT_TRANSACTION_FAILED, 
                    "적금 가입 중 거래 처리에 실패했습니다: " + e.getMessage());
            }
        } else {
            log.info("가입금액이 0원이므로 출금/입금 처리를 건너뜁니다.");
        }

        return savedAccount;
    }

    public List<SavingsAccount> getUserSavingsAccounts(Long userId) {
        User user = userService.getUserById(userId);
        return savingsAccountRepository.findByUser(user);
    }

    public SavingsAccount getSavingsAccountByNumber(String accountNumber) {
        return savingsAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVINGS_ACCOUNT_NOT_FOUND));
    }

    @Transactional
    public SavingsAccount depositToSavings(String accountNumber, Long amount) {
        SavingsAccount account = getSavingsAccountByNumber(accountNumber);
        
        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
        
        account.deposit(amount);
        SavingsAccount updatedAccount = savingsAccountRepository.save(account);
        
        log.info("적금 계좌 입금 완료 - 계좌번호: {}, 금액: {}, 잔고: {}", 
                accountNumber, amount, updatedAccount.getBalance());
        
        return updatedAccount;
    }

    @Transactional
    public SavingsAccount withdrawFromSavings(String accountNumber, Long amount) {
        SavingsAccount account = getSavingsAccountByNumber(accountNumber);
        
        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
        
        if (account.getBalance() < amount) { // Long 타입으로 비교
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        
        account.withdraw(amount);
        SavingsAccount updatedAccount = savingsAccountRepository.save(account);
        
        log.info("적금 계좌 출금 완료 - 계좌번호: {}, 금액: {}, 잔고: {}", 
                accountNumber, amount, updatedAccount.getBalance());
        
        return updatedAccount;
    }

    @Transactional
    public SavingsAccount closeSavingsAccount(String accountNumber) {
        SavingsAccount account = getSavingsAccountByNumber(accountNumber);
        
        if (account.getBalance() > 0) { // Long 타입으로 비교
            throw new BusinessException(ErrorCode.SAVINGS_ACCOUNT_HAS_BALANCE);
        }
        
        account.close();
        SavingsAccount updatedAccount = savingsAccountRepository.save(account);
        
        log.info("적금 계좌 해지 완료 - 계좌번호: {}", accountNumber);
        
        return updatedAccount;
    }

    private String generateAccountNumber() {
        String prefix = "506";
        String middle = String.format("%06d", (int) (Math.random() * 1000000));
        String suffix = String.format("%05d", (int) (Math.random() * 100000));
        return prefix + "-" + middle + "-" + suffix;
    }
}