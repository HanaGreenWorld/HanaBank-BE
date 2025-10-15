package com.kopo.hanabank.integration.service;

import com.kopo.hanabank.common.exception.BusinessException;
import com.kopo.hanabank.common.exception.ErrorCode;
import com.kopo.hanabank.deposit.domain.DemandDepositAccount;
import com.kopo.hanabank.deposit.dto.DemandDepositAccountResponse;
import com.kopo.hanabank.deposit.repository.DemandDepositAccountRepository;
import com.kopo.hanabank.integration.dto.BankCustomerInfoResponse;
import com.kopo.hanabank.integration.dto.IntegratedFinancialProductsResponse;
import com.kopo.hanabank.investment.domain.InvestmentAccount;
import com.kopo.hanabank.investment.repository.InvestmentAccountRepository;
import com.kopo.hanabank.loan.domain.LoanAccount;
import com.kopo.hanabank.loan.repository.LoanAccountRepository;
import com.kopo.hanabank.savings.domain.SavingsAccount;
import com.kopo.hanabank.savings.dto.SavingsAccountCreateRequest;
import com.kopo.hanabank.savings.dto.SavingsAccountResponse;
import com.kopo.hanabank.savings.repository.SavingsAccountRepository;
import com.kopo.hanabank.savings.service.SavingsService;
import com.kopo.hanabank.user.domain.User;
import com.kopo.hanabank.user.repository.UserRepository;
import java.util.Base64;
import com.kopo.hanabank.electronicreceipt.repository.ElectronicReceiptRepository;
import com.kopo.hanabank.electronicreceipt.domain.ElectronicReceipt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BankIntegrationService {

    private final UserRepository userRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final InvestmentAccountRepository investmentAccountRepository;
    private final SavingsService savingsService;
    private final ElectronicReceiptRepository electronicReceiptRepository;


    public BankCustomerInfoResponse getCustomerInfo(String customerInfoToken, String requestingService) {
        try {
            log.info("고객 정보 조회 시작 - 고객정보토큰: {}, 요청서비스: {}", customerInfoToken, requestingService);

            String ci = new String(Base64.getDecoder().decode(customerInfoToken));
            log.info("추출된 CI: {}", ci);

            User user = userRepository.findByCi(ci)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 계좌 정보 조회
            List<BankCustomerInfoResponse.AccountInfo> accounts = getAccountInfo(user);

            // 상품 정보 조회
            List<BankCustomerInfoResponse.ProductInfo> products = getProductInfo(user);

            // 총 잔액 계산
            BigDecimal totalBalance = calculateTotalBalance(user);

            return BankCustomerInfoResponse.builder()
                    .customerId(user.getId())
                    .customerName(user.getName())
                    .phoneNumber(user.getPhoneNumber())
                    .accounts(accounts)
                    .products(products)
                    .responseTime(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("고객 정보 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    public IntegratedFinancialProductsResponse getIntegratedProducts(String customerInfoToken, String requestingService) {
        try {
            log.info("통합 금융 상품 조회 시작 - 고객정보토큰: {}, 요청서비스: {}", customerInfoToken, requestingService);

            String ci = new String(Base64.getDecoder().decode(customerInfoToken));

            User user = userRepository.findByCi(ci)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 각 상품별 정보 조회
            List<IntegratedFinancialProductsResponse.SavingsProduct> savingsProducts = getSavingsProducts(user);
            List<IntegratedFinancialProductsResponse.LoanProduct> loanProducts = getLoanProducts(user);
            List<IntegratedFinancialProductsResponse.InvestmentProduct> investmentProducts = getInvestmentProducts(user);

            return IntegratedFinancialProductsResponse.builder()
                    .customerId(user.getId())
                    .customerName(user.getName())
                    .savingsProducts(savingsProducts)
                    .loanProducts(loanProducts)
                    .investmentProducts(investmentProducts)
                    .lastUpdated(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("통합 금융 상품 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getProductStatus(String phoneNumber) {
        try {
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 각 상품별 현황 조회
            long savingsCount = savingsAccountRepository.findByUser(user).size();
            long loanCount = loanAccountRepository.findByUser(user).size();
            long investmentCount = investmentAccountRepository.findByUser(user).size();
            long depositCount = demandDepositAccountRepository.findActiveAccountsByUser(user).size();

            Map<String, Object> status = Map.of(
                    "savingsCount", savingsCount,
                    "loanCount", loanCount,
                    "investmentCount", investmentCount,
                    "depositCount", depositCount,
                    "totalProducts", savingsCount + loanCount + investmentCount + depositCount
            );

            return status;

        } catch (Exception e) {
            throw new RuntimeException("상품 현황 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    private List<BankCustomerInfoResponse.AccountInfo> getAccountInfo(User user) {
        List<BankCustomerInfoResponse.AccountInfo> accounts = new ArrayList<>();

        // 입출금 계좌 조회
        List<DemandDepositAccount> demandDepositAccounts = demandDepositAccountRepository.findActiveAccountsByUser(user);
        accounts.addAll(demandDepositAccounts.stream()
                .map(account -> BankCustomerInfoResponse.AccountInfo.builder()
                        .accountNumber(account.getAccountNumber())
                        .accountType("DEMAND_DEPOSIT")
                        .accountName("입출금예금")
                        .balance(new BigDecimal(account.getBalance()))
                        .openDate(account.getCreatedAt())
                        .status(account.getStatus().toString())
                        .build())
                .collect(java.util.stream.Collectors.toList()));
        return accounts;
    }

    private List<BankCustomerInfoResponse.ProductInfo> getLoanProductsForCustomerInfo(User user) {
        List<LoanAccount> loanAccounts = loanAccountRepository.findByUser(user);
        return loanAccounts.stream()
                .map(loan -> BankCustomerInfoResponse.ProductInfo.builder()
                        .productId(loan.getId())
                        .productName(loan.getProduct().getProductName())
                        .productType("LOAN")
                        .productCode(loan.getAccountNumber())
                        .amount(new BigDecimal(loan.getLoanAmount()))
                        .remainingAmount(new BigDecimal(loan.getRemainingAmount()))
                        .interestRate(loan.getInterestRate())
                        .monthlyPayment(new BigDecimal(loan.getMonthlyPayment()))
                        .startDate(loan.getStartDate().atStartOfDay())
                        .maturityDate(loan.getMaturityDate().atStartOfDay())
                        .subscriptionDate(loan.getCreatedAt())
                        .status(loan.getStatus().toString())
                        .build())
                .collect(Collectors.toList());
    }

    private List<BankCustomerInfoResponse.ProductInfo> getSavingsProductsForCustomerInfo(User user) {
        try {
            List<SavingsAccount> savingsAccounts = savingsAccountRepository.findByUser(user);

            return savingsAccounts.stream()
                    .map(account -> {
                        try {
                            return BankCustomerInfoResponse.ProductInfo.builder()
                                    .productId(account.getId())
                                    .productName(account.getProduct() != null ? account.getProduct().getProductName() : "알 수 없음")
                                    .productType("SAVINGS")
                                    .productCode(account.getAccountNumber())
                                    .amount(new BigDecimal(account.getBalance()))
                                    .interestRate(account.getFinalRate())
                                    .baseRate(account.getBaseRate())
                                    .preferentialRate(account.getPreferentialRate())
                                    .startDate(account.getStartDate().atStartOfDay())
                                    .maturityDate(account.getMaturityDate().atStartOfDay())
                                    .subscriptionDate(account.getCreatedAt())
                                    .status(account.getStatus().toString())
                                    .build();
                        } catch (Exception e) {
                            log.error("적금 계좌 처리 중 오류: {}", e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(product -> product != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("적금 계좌 조회 중 오류: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private List<BankCustomerInfoResponse.ProductInfo> getInvestmentProductsForCustomerInfo(User user) {
        List<InvestmentAccount> investmentAccounts = investmentAccountRepository.findByUser(user);
        return investmentAccounts.stream()
                .map(account -> BankCustomerInfoResponse.ProductInfo.builder()
                        .productId(account.getId())
                        .productName(account.getProduct().getProductName())
                        .productType("INVESTMENT")
                        .amount(new BigDecimal(account.getCurrentValue()))
                        .status(account.getStatus().toString())
                        .subscriptionDate(account.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private List<IntegratedFinancialProductsResponse.SavingsProduct> getSavingsProducts(User user) {
        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findByUser(user);
        return savingsAccounts.stream()
                .map(account -> IntegratedFinancialProductsResponse.SavingsProduct.builder()
                        .productId(account.getId())
                        .productName(account.getProduct().getProductName())
                        .accountNumber(account.getAccountNumber())
                        .balance(new BigDecimal(account.getBalance()))
                        .interestRate(account.getFinalRate())
                        .maturityDate(account.getMaturityDate().atStartOfDay())
                        .status(account.getStatus().toString())
                        .build())
                .collect(Collectors.toList());
    }

    private List<IntegratedFinancialProductsResponse.LoanProduct> getLoanProducts(User user) {
        List<LoanAccount> loanAccounts = loanAccountRepository.findByUser(user);
        return loanAccounts.stream()
                .map(loan -> IntegratedFinancialProductsResponse.LoanProduct.builder()
                        .productId(loan.getId())
                        .productName(loan.getProduct().getProductName())
                        .loanAmount(new BigDecimal(loan.getLoanAmount()))
                        .interestRate(loan.getInterestRate())
                        .remainingAmount(new BigDecimal(loan.getRemainingAmount()))
                        .status(loan.getStatus().toString())
                        .build())
                .collect(Collectors.toList());
    }

    private List<IntegratedFinancialProductsResponse.InvestmentProduct> getInvestmentProducts(User user) {
        List<InvestmentAccount> investmentAccounts = investmentAccountRepository.findByUser(user);
        return investmentAccounts.stream()
                .map(account -> IntegratedFinancialProductsResponse.InvestmentProduct.builder()
                        .productId(account.getId())
                        .productName(account.getProduct().getProductName())
                        .investmentAmount(new BigDecimal(account.getInvestmentAmount()))
                        .currentValue(new BigDecimal(account.getCurrentValue()))
                        .returnRate(new BigDecimal(account.getCurrentValue() - account.getInvestmentAmount()))
                        .status(account.getStatus().toString())
                        .build())
                .collect(Collectors.toList());
    }

    private List<BankCustomerInfoResponse.ProductInfo> getProductInfo(User user) {
        List<BankCustomerInfoResponse.ProductInfo> allProducts = new ArrayList<>();
        allProducts.addAll(getSavingsProductsForCustomerInfo(user));
        allProducts.addAll(getLoanProductsForCustomerInfo(user));
        allProducts.addAll(getInvestmentProductsForCustomerInfo(user));
        return allProducts;
    }

    private BigDecimal calculateTotalBalance(User user) {
        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findByUser(user);
        List<LoanAccount> loanAccounts = loanAccountRepository.findByUser(user);
        List<InvestmentAccount> investmentAccounts = investmentAccountRepository.findByUser(user);
        
        BigDecimal totalSavings = savingsAccounts.stream()
                .filter(account -> account.getIsActive())
                .map(account -> new BigDecimal(account.getBalance()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalLoans = loanAccounts.stream()
                .map(loan -> new BigDecimal(loan.getLoanAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalInvestments = investmentAccounts.stream()
                .map(investment -> new BigDecimal(investment.getCurrentValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalSavings.add(totalInvestments).subtract(totalLoans);
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 8) {
            return accountNumber;
        }
        return accountNumber.substring(0, 4) + "****" + accountNumber.substring(accountNumber.length() - 4);
    }

    public Object getAccountBalance(String phoneNumber) {
        try {
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            // 입출금 계좌 잔액 조회
            List<DemandDepositAccount> demandAccounts = demandDepositAccountRepository.findActiveAccountsByUser(user);
            Long totalBalance = demandAccounts.stream()
                    .mapToLong(DemandDepositAccount::getBalance)
                    .sum();
            
            log.info("계좌 잔액 조회 완료 - 총 잔액: {}", totalBalance);
            return Map.of("totalBalance", totalBalance, "accountCount", demandAccounts.size());
            
        } catch (Exception e) {
            throw new RuntimeException("계좌 잔액 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    public boolean checkProductOwnership(String customerInfoToken, Long productId) {
        try {
            String ci = new String(Base64.getDecoder().decode(customerInfoToken));

            User user = userRepository.findByCi(ci)
                    .orElse(null);

            if (user == null) {
                log.warn("사용자를 찾을 수 없음 - CI: {}", ci);
                return false;
            }

            // productId에 따른 상품 보유 여부 확인
            if (productId == 1L) {
                // productId 1: 하나green세상 적금
                List<SavingsAccount> savingsAccounts = savingsAccountRepository.findByUser(user);
                boolean hasProduct = savingsAccounts.stream()
                        .anyMatch(account ->
                            account.getIsActive() &&
                            account.getStatus() == SavingsAccount.AccountStatus.ACTIVE &&
                            account.getProduct().getId().equals(productId)
                        );

                log.info("사용자 {}의 productId {} 보유 여부: {}", user.getName(), productId, hasProduct);
                return hasProduct;
            }

            // 다른 상품 타입들은 추후 구현
            log.warn("지원하지 않는 상품ID: {}", productId);
            return false;

        } catch (Exception e) {
            log.error("상품 보유 여부 확인 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    public static class SavingsAccountCreateRequest {
        private Long userId;
        private Long productId;
        private BigDecimal preferentialRate;
        private Long applicationAmount;

        public SavingsAccountCreateRequest() {}

        public SavingsAccountCreateRequest(Long userId, Long productId, BigDecimal preferentialRate, Long applicationAmount) {
            this.userId = userId;
            this.productId = productId;
            this.preferentialRate = preferentialRate;
            this.applicationAmount = applicationAmount;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public BigDecimal getPreferentialRate() { return preferentialRate; }
        public void setPreferentialRate(BigDecimal preferentialRate) { this.preferentialRate = preferentialRate; }
        public Long getApplicationAmount() { return applicationAmount; }
        public void setApplicationAmount(Long applicationAmount) { this.applicationAmount = applicationAmount; }
    }

    @Transactional
    public SavingsAccountResponse createSavingsAccountByToken(Long productId, BigDecimal preferentialRate, Long applicationAmount, String ci,
                                                           Boolean autoTransferEnabled, Integer transferDay, Long monthlyTransferAmount,
                                                           String withdrawalAccountNumber, String withdrawalBankName) {
        try {
            User user = userRepository.findByCi(ci)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            // 적금 계좌 생성 (자동이체 설정 포함)
            SavingsAccount account = savingsService.createSavingsAccountWithAutoTransfer(
                    user.getId(),
                    productId,
                    preferentialRate,
                    applicationAmount,
                    autoTransferEnabled != null ? autoTransferEnabled : false,
                    transferDay,
                    monthlyTransferAmount,
                    withdrawalAccountNumber,
                    withdrawalBankName
            );

            return new SavingsAccountResponse(account);
            
        } catch (Exception e) {
            log.error("토큰으로 적금 계좌 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("적금 계좌 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> getElectronicReceiptsByCI(String customerInfoToken) {
        try {
            String ci = new String(Base64.getDecoder().decode(customerInfoToken));

            User user = userRepository.findByCi(ci)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            List<ElectronicReceipt> receipts = electronicReceiptRepository
                    .findByCustomerIdOrderByReceiptDateDesc(user.getId());

            List<Map<String, Object>> result = receipts.stream()
                    .map(this::convertToMap)
                    .collect(Collectors.toList());

            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("전자영수증 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> convertToMap(ElectronicReceipt receipt) {
        Map<String, Object> map = new HashMap<>();
        map.put("receiptId", receipt.getReceiptId());
        map.put("customerId", receipt.getCustomerId());
        map.put("transactionId", receipt.getTransactionId());
        map.put("transactionType", receipt.getTransactionType().name());
        map.put("transactionAmount", receipt.getTransactionAmount());
        map.put("branchName", receipt.getBranchName());
        map.put("receiptDate", receipt.getReceiptDate());
        map.put("isGreenWorldUser", receipt.getIsGreenWorldUser());
        map.put("webhookSent", receipt.getWebhookSent());
        map.put("webhookSentAt", receipt.getWebhookSentAt());
        map.put("createdAt", receipt.getCreatedAt());
        return map;
    }

    public List<Map<String, Object>> getDepositAccountsByCi(String ci) {
        try {
            User user = userRepository.findByCi(ci)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 사용자의 입출금 계좌 조회
            List<DemandDepositAccount> accounts = demandDepositAccountRepository.findByUser(user);

            List<Map<String, Object>> accountList = new ArrayList<>();
            for (DemandDepositAccount account : accounts) {
                Map<String, Object> accountMap = new HashMap<>();
                accountMap.put("accountNumber", account.getAccountNumber());
                accountMap.put("accountName", account.getAccountName());
                accountMap.put("balance", account.getBalance());
                accountMap.put("accountType", account.getAccountType().name());
                accountMap.put("isActive", account.getIsActive());
                accountMap.put("openDate", account.getOpenDate());
                accountMap.put("bankCode", account.getBankCode());
                accountList.add(accountMap);
            }

            log.info("입출금 계좌 목록 조회 완료 - 계좌수: {}", accountList.size());
            return accountList;

        } catch (Exception e) {
            throw new RuntimeException("입출금 계좌 목록 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

}

