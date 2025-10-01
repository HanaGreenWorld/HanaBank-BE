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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 고객 정보 조회 (그룹 토큰으로)
     */
    public BankCustomerInfoResponse getCustomerInfoByGroupToken(String groupCustomerToken, String infoType) {
        try {
            log.info("고객 정보 조회 시작 - 그룹토큰: {}, 정보타입: {}", groupCustomerToken, infoType);
            
            // 토큰에서 전화번호 추출
            String phoneNumber = extractPhoneFromGroupToken(groupCustomerToken);
            log.info("추출된 전화번호: {}", phoneNumber);
            
            // 사용자 조회
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            // 계좌 정보 조회
            List<BankCustomerInfoResponse.AccountInfo> accounts = getAccountInfo(user);
            
            // 상품 정보 조회
            List<BankCustomerInfoResponse.ProductInfo> products = getProductInfo(user);
            
            // 응답 생성
            BankCustomerInfoResponse response = BankCustomerInfoResponse.builder()
                    .customerId(user.getId())
                    .customerName(user.getName())
                    .phoneNumber(user.getPhoneNumber())
                    .email(user.getEmail())
                    .customerGrade("VIP")
                    .status("ACTIVE")
                    .joinDate(user.getCreatedAt())
                    .accounts(accounts)
                    .products(products)
                    .responseTime(LocalDateTime.now())
                    .build();
            
            log.info("고객 정보 조회 완료: {}", response);
            return response;
            
        } catch (Exception e) {
            log.error("고객 정보 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("고객 정보 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 고객 정보 조회
     */
    public BankCustomerInfoResponse getCustomerInfo(String groupCustomerToken, String requestingService) {
        try {
            log.info("고객 정보 조회 시작 - 그룹토큰: {}, 요청서비스: {}", groupCustomerToken, requestingService);

            // Group Customer Token에서 전화번호 추출
            String phoneNumber = extractPhoneFromGroupToken(groupCustomerToken);
            log.info("추출된 전화번호: {}", phoneNumber);

            // 사용자 조회
            User user = userRepository.findByPhoneNumber(phoneNumber)
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
            log.error("고객 정보 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("고객 정보 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 통합 금융 상품 조회
     */
    public IntegratedFinancialProductsResponse getIntegratedProducts(String groupCustomerToken, String requestingService) {
        try {
            log.info("통합 금융 상품 조회 시작 - 그룹토큰: {}, 요청서비스: {}", groupCustomerToken, requestingService);

            // Group Customer Token에서 전화번호 추출
            String phoneNumber = extractPhoneFromGroupToken(groupCustomerToken);
            log.info("추출된 전화번호: {}", phoneNumber);

            // 사용자 조회
            User user = userRepository.findByPhoneNumber(phoneNumber)
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
            log.error("통합 금융 상품 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("통합 금융 상품 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 상품 현황 조회
     */
    public Map<String, Object> getProductStatus(String phoneNumber) {
        try {
            // 사용자 조회
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
            log.error("상품 현황 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("상품 현황 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 입출금 및 적금 계좌 정보 조회
     */
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

        // 적금 계좌 조회
        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findByUser(user);
        accounts.addAll(savingsAccounts.stream()
                .map(account -> BankCustomerInfoResponse.AccountInfo.builder()
                        .accountNumber(account.getAccountNumber())
                        .accountType("SAVINGS")
                        .accountName(account.getProduct().getProductName())
                        .balance(new BigDecimal(account.getBalance()))
                        .openDate(account.getCreatedAt())
                        .status(account.getStatus().toString())
                        .build())
                .collect(java.util.stream.Collectors.toList()));

        return accounts;
    }

    /**
     * 대출 상품 정보 조회 - 실제 DB 데이터 사용
     */
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

    /**
     * 적금 상품 정보 조회 (고객 정보용)
     */
    private List<BankCustomerInfoResponse.ProductInfo> getSavingsProductsForCustomerInfo(User user) {
        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findByUser(user);
        return savingsAccounts.stream()
                .map(account -> BankCustomerInfoResponse.ProductInfo.builder()
                        .productId(account.getId())
                        .productName(account.getProduct().getProductName())
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
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 투자 상품 정보 조회
     */
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

    /**
     * 적금 상품 조회
     */
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

    /**
     * 대출 상품 조회
     */
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

    /**
     * 투자 상품 조회
     */
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

    /**
     * 상품 정보 조회
     */
    private List<BankCustomerInfoResponse.ProductInfo> getProductInfo(User user) {
        List<BankCustomerInfoResponse.ProductInfo> allProducts = new ArrayList<>();
        allProducts.addAll(getSavingsProductsForCustomerInfo(user));
        allProducts.addAll(getLoanProductsForCustomerInfo(user));
        allProducts.addAll(getInvestmentProductsForCustomerInfo(user));
        return allProducts;
    }

    /**
     * 총 잔액 계산
     */
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
    
    /**
     * Group Customer Token에서 전화번호 추출
     */
    private String extractPhoneFromGroupToken(String groupCustomerToken) {
        try {
            log.info("🔍 토큰에서 전화번호 추출 시작: {}", groupCustomerToken);
            
            // Base64 디코딩 시도
            String decodedToken;
            try {
                decodedToken = new String(java.util.Base64.getDecoder().decode(groupCustomerToken));
                log.info("🔍 Base64 디코딩 결과: {}", decodedToken);
            } catch (Exception e) {
                // Base64 디코딩 실패 시 원본 토큰 사용
                decodedToken = groupCustomerToken;
                log.info("🔍 Base64 디코딩 실패, 원본 토큰 사용: {}", decodedToken);
            }
            
            // 토큰에서 전화번호 추출
            String phoneNumber;
            if (decodedToken != null && decodedToken.contains("_")) {
                String[] parts = decodedToken.split("_");
                if (parts.length > 1) {
                    phoneNumber = parts[1];
                    log.info("🔍 추출된 전화번호: {}", phoneNumber);
                } else {
                    phoneNumber = "01099999999"; // 기본값
                    log.warn("🔍 토큰 파싱 실패, 기본값 사용: {}", phoneNumber);
                }
            } else {
                phoneNumber = "01099999999"; // 기본값
                log.warn("🔍 토큰 형식 오류, 기본값 사용: {}", phoneNumber);
            }
            
            // 하이픈 제거하여 통일된 형식으로 반환
            String finalPhoneNumber = phoneNumber.replaceAll("-", "");
            log.info("🔍 최종 전화번호: {}", finalPhoneNumber);
            return finalPhoneNumber;
            
        } catch (Exception e) {
            log.error("🔍 토큰에서 전화번호 추출 실패", e);
            return "01099999999"; // 기본값
        }
    }

    /**
     * 계좌번호 마스킹
     */
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 8) {
            return accountNumber;
        }
        return accountNumber.substring(0, 4) + "****" + accountNumber.substring(accountNumber.length() - 4);
    }

    /**
     * 계좌 잔액 조회
     */
    public Object getAccountBalance(String phoneNumber) {
        try {
            log.info("계좌 잔액 조회 시작 - 전화번호: {}", phoneNumber);
            
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
            log.error("계좌 잔액 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("계좌 잔액 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 입출금 계좌 목록 조회
     */
    public List<DemandDepositAccountResponse> getDepositAccounts(String phoneNumber) {
        try {
            log.info("입출금 계좌 목록 조회 요청 - 전화번호: {}", phoneNumber);

            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            List<DemandDepositAccount> accounts = demandDepositAccountRepository.findActiveAccountsByUser(user);

            return accounts.stream()
                    .map(DemandDepositAccountResponse::from)
                    .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            log.error("입출금 계좌 목록 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("입출금 계좌 목록 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 특정 상품 보유 여부 확인
     */
    public boolean checkProductOwnership(String groupCustomerToken, Long productId) {
        try {
            log.info("상품 보유 여부 확인 - 그룹토큰: {}, 상품ID: {}", groupCustomerToken, productId);

            // Group Customer Token에서 전화번호 추출
            String phoneNumber = extractPhoneFromGroupToken(groupCustomerToken);
            log.info("추출된 전화번호: {}", phoneNumber);

            // 사용자 조회
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElse(null);

            if (user == null) {
                log.warn("사용자를 찾을 수 없음 - 전화번호: {}", phoneNumber);
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

    // 요청 DTO 클래스
    public static class SavingsAccountCreateRequest {
        private Long userId;
        private Long productId;
        private BigDecimal preferentialRate;
        private Long applicationAmount;

        // 생성자, getter, setter
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

    /**
     * 토큰으로 적금 계좌 생성 (자동이체 설정 포함)
     */
    @Transactional
    public SavingsAccountResponse createSavingsAccountByToken(Long productId, BigDecimal preferentialRate, Long applicationAmount, String phoneNumber,
                                                           Boolean autoTransferEnabled, Integer transferDay, Long monthlyTransferAmount,
                                                           String withdrawalAccountNumber, String withdrawalBankName) {
        try {
            log.info("토큰으로 적금 계좌 생성 시작 - 상품ID: {}, 전화번호: {}, 자동이체: {}", productId, phoneNumber, autoTransferEnabled);
            
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            // 적금 계좌 생성 (자동이체 설정 포함)
            // 사용자가 선택한 출금계좌 정보를 그대로 사용
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
            
            log.info("토큰으로 적금 계좌 생성 완료 - 계좌번호: {}, 자동이체: {}", account.getAccountNumber(), autoTransferEnabled);
            return new SavingsAccountResponse(account);
            
        } catch (Exception e) {
            log.error("토큰으로 적금 계좌 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("적금 계좌 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }

}

