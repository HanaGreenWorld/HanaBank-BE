package com.kopo.hanabank.investment.service;

import com.kopo.hanabank.common.exception.BusinessException;
import com.kopo.hanabank.common.exception.ErrorCode;
import com.kopo.hanabank.investment.domain.InvestmentAccount;
import com.kopo.hanabank.investment.domain.InvestmentProduct;
import com.kopo.hanabank.investment.repository.InvestmentAccountRepository;
import com.kopo.hanabank.investment.repository.InvestmentProductRepository;
import com.kopo.hanabank.user.domain.User;
import com.kopo.hanabank.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvestmentService {

    private final InvestmentProductRepository investmentProductRepository;
    private final InvestmentAccountRepository investmentAccountRepository;
    private final UserService userService;

    public List<InvestmentProduct> getAllInvestmentProducts() {
        return investmentProductRepository.findByIsActiveTrue();
    }

    public List<InvestmentProduct> getInvestmentProductsByType(InvestmentProduct.ProductType productType) {
        return investmentProductRepository.findByIsActiveTrueAndProductType(productType);
    }

    public List<InvestmentProduct> getInvestmentProductsByRiskLevel(Integer riskLevel) {
        return investmentProductRepository.findByIsActiveTrueAndRiskLevelLessThanEqual(riskLevel);
    }

    public InvestmentProduct getInvestmentProductById(Long id) {
        return investmentProductRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVESTMENT_PRODUCT_NOT_FOUND));
    }

    @Transactional
    public InvestmentAccount createInvestmentAccount(Long userId, Long productId, Long investmentAmount) {
        User user = userService.getUserById(userId);
        InvestmentProduct product = getInvestmentProductById(productId);

        // 투자 금액 검증
        if (investmentAmount < product.getMinInvestmentAmount()) {
            throw new BusinessException(ErrorCode.INVALID_INVESTMENT_AMOUNT);
        }
        if (product.getMaxInvestmentAmount() != null && investmentAmount > product.getMaxInvestmentAmount()) {
            throw new BusinessException(ErrorCode.INVALID_INVESTMENT_AMOUNT);
        }

        // 계좌번호 생성
        String accountNumber = generateAccountNumber();

        // 중복 체크
        if (investmentAccountRepository.existsByAccountNumber(accountNumber)) {
            throw new BusinessException(ErrorCode.INVESTMENT_ACCOUNT_NOT_FOUND);
        }

        InvestmentAccount account = InvestmentAccount.builder()
                .user(user)
                .product(product)
                .accountNumber(accountNumber)
                .investmentAmount(investmentAmount)
                .startDate(LocalDate.now())
                .build();

        return investmentAccountRepository.save(account);
    }

    public List<InvestmentAccount> getUserInvestmentAccounts(Long userId) {
        User user = userService.getUserById(userId);
        return investmentAccountRepository.findByUser(user);
    }

    public InvestmentAccount getInvestmentAccountByNumber(String accountNumber) {
        return investmentAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVESTMENT_ACCOUNT_NOT_FOUND));
    }

    @Transactional
    public InvestmentAccount invest(String accountNumber, Long amount) {
        InvestmentAccount account = getInvestmentAccountByNumber(accountNumber);
        
        if (account.getStatus() != InvestmentAccount.AccountStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.INVALID_INVESTMENT_AMOUNT);
        }

        account.invest(amount);
        return account;
    }

    @Transactional
    public InvestmentAccount redeem(String accountNumber, Long amount) {
        InvestmentAccount account = getInvestmentAccountByNumber(accountNumber);
        
        if (account.getStatus() != InvestmentAccount.AccountStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.INVALID_INVESTMENT_AMOUNT);
        }

        account.redeem(amount);
        return account;
    }

    @Transactional
    public InvestmentAccount updateCurrentValue(String accountNumber, Long currentValue) {
        InvestmentAccount account = getInvestmentAccountByNumber(accountNumber);
        account.updateCurrentValue(currentValue);
        return account;
    }

    @Transactional
    public void closeInvestmentAccount(String accountNumber) {
        InvestmentAccount account = getInvestmentAccountByNumber(accountNumber);
        account.close();
    }

    private String generateAccountNumber() {
        return "INV" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}












