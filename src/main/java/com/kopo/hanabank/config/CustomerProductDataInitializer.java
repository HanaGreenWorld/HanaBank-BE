////package com.kopo.hanabank.config;
//import com.kopo.hanabank.deposit.domain.DemandDepositAccount;
//import com.kopo.hanabank.deposit.repository.DemandDepositAccountRepository;
//import com.kopo.hanabank.user.domain.User;
//import com.kopo.hanabank.user.repository.UserRepository;
//import com.kopo.hanabank.savings.domain.SavingsAccount;
//import com.kopo.hanabank.savings.repository.SavingsAccountRepository;
//import com.kopo.hanabank.savings.domain.SavingsProduct;
//import com.kopo.hanabank.savings.repository.SavingsProductRepository;
//import com.kopo.hanabank.loan.domain.LoanAccount;
//import com.kopo.hanabank.loan.repository.LoanAccountRepository;
//import com.kopo.hanabank.loan.domain.LoanProduct;
//import com.kopo.hanabank.loan.repository.LoanProductRepository;
//import com.kopo.hanabank.investment.domain.InvestmentAccount;
//import com.kopo.hanabank.investment.repository.InvestmentAccountRepository;
//import com.kopo.hanabank.investment.domain.InvestmentProduct;
//import com.kopo.hanabank.investment.repository.InvestmentProductRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class CustomerProductDataInitializer implements CommandLineRunner {
////    private final UserRepository userRepository;
////    private final SavingsAccountRepository savingsAccountRepository;
////    private final SavingsProductRepository savingsProductRepository;
////    private final DemandDepositAccountRepository demandDepositAccountRepository;
////    private final LoanAccountRepository loanAccountRepository;
////    private final LoanProductRepository loanProductRepository;
////    private final InvestmentAccountRepository investmentAccountRepository;
////    private final InvestmentProductRepository investmentProductRepository;
////
////    @Override
////    public void run(String... args) throws Exception {
////        // 테스트 사용자 및 상품 데이터 생성
////        log.info("하나은행 서버 시작 - 테스트 데이터 초기화 시작");
////
////        User testCustomer = createTestCustomer();
////        createCustomerProducts(testCustomer);
////
////        // hanagreenworld 사용자 ID 7에 해당하는 사용자도 상품 생성
////        String phoneNumber2 = "01099999999";
////        User testCustomer2 = userRepository.findByPhoneNumber(phoneNumber2).orElse(null);
////        if (testCustomer2 != null) {
////            createCustomerProducts(testCustomer2);
////        }
////
////        log.info("하나은행 서버 시작 - 테스트 데이터 초기화 완료");
////    }
//////
//////    private User createTestCustomer() {
//////        // 전화번호로 사용자 조회 (프론트엔드에서 사용할 수 있도록)
//////        String phoneNumber = "010-1234-5678";
//////
//////        User user1 = userRepository.findByPhoneNumber(phoneNumber)
//////                .orElseGet(() -> {
//////                    User user = User.builder()
//////                            .username("green_user")
//////                            .name("김그린")
//////                            .email("green@example.com")
//////                            .phoneNumber(phoneNumber)
//////                            .birthDate("19900315")
//////                            .customerGrade("GOLD")
//////                            .isActive(true)
//////                            .build();
//////                    User savedUser = userRepository.save(user);
//////                    log.info("테스트 사용자 생성 완료 - ID: {}, 전화번호: {}", savedUser.getId(), phoneNumber);
//////                    return savedUser;
//////                });
//////
//////        // hanagreenworld 사용자 ID 7에 해당하는 사용자도 생성
//////        String phoneNumber2 = "01099999999";
//////        User user2 = userRepository.findByPhoneNumber(phoneNumber2)
//////                .orElseGet(() -> {
//////                    User user = User.builder()
//////                            .username("test_user")
//////                            .name("테스트사용자")
//////                            .email("test@example.com")
//////                            .phoneNumber(phoneNumber2)
//////                            .birthDate("19900101")
//////                            .customerGrade("GOLD")
//////                            .isActive(true)
//////                            .build();
//////                    User savedUser = userRepository.save(user);
//////                    log.info("테스트 사용자 생성 완료 - ID: {}, 전화번호: {}", savedUser.getId(), phoneNumber2);
//////                    return savedUser;
//////                });
//////
//////        return user1;
//////    }
//////
//////    private void createCustomerProducts(User user) {
//////        // 입출금 계좌 생성
//////        if (demandDepositAccountRepository.findByUser(user).isEmpty()) {
//////            // 계좌번호 중복 체크
//////            String demandAccountNumber = "081-1234-5678-02";
//////            boolean demandAccountExists = demandDepositAccountRepository.findByAccountNumber(demandAccountNumber).isPresent();
//////
//////            if (!demandAccountExists) {
//////                List<DemandDepositAccount> demandAccounts = Arrays.asList(
//////                    DemandDepositAccount.builder()
//////                        .user(user)
//////                        .accountNumber(demandAccountNumber)
//////                        .accountName("하나 입출금통장")
//////                        .bankCode("081")
//////                        .accountType(DemandDepositAccount.AccountType.CHECKING)
//////                        .balance(1000000L) // 100만원 초기 잔액
//////                        .availableBalance(1000000L) // 사용 가능 잔액
//////                        .openDate(LocalDate.now().minusMonths(6))
//////                        .isActive(true)
//////                        .status(DemandDepositAccount.AccountStatus.ACTIVE)
//////                        .build()
//////                );
//////                demandDepositAccountRepository.saveAll(demandAccounts);
//////                log.info("고객 {}의 입출금 계좌 {}개가 생성되었습니다.", user.getName(), demandAccounts.size());
//////            } else {
//////                log.info("입출금 계좌가 이미 존재합니다. 생성을 건너뜁니다.");
//////            }
//////        }
//////
//////        // 적금 계좌 생성
//////        if (savingsAccountRepository.findByUser(user).isEmpty()) {
//////            // 적금 상품 조회 - 지구사랑적금 찾기
//////            List<SavingsProduct> savingsProducts = savingsProductRepository.findAll();
//////            SavingsProduct earthProduct = savingsProducts.stream()
//////                .filter(product -> "지구 사랑 적금".equals(product.getProductName()))
//////                .findFirst()
//////                .orElse(savingsProducts.get(0)); // 지구사랑적금이 없으면 첫 번째 상품 사용
//////
//////            // 계좌번호 중복 체크
//////            String accountNumber = "110-444-555666";
//////            boolean accountExists = savingsAccountRepository.findByAccountNumber(accountNumber).isPresent();
//////
//////            if (!accountExists) {
//////                List<SavingsAccount> savingsAccounts = Arrays.asList(
//////                    SavingsAccount.builder()
//////                        .user(user)
//////                        .product(earthProduct)
//////                        .accountNumber(accountNumber)
//////                        .accountName("지구 사랑 적금")
//////                        .startDate(LocalDate.of(2024, 3, 1))
//////                        .maturityDate(LocalDate.of(2026, 3, 1))
//////                        .baseRate(earthProduct.getBasicRate()) // 상품의 기본금리 사용 (1.8%)
//////                        .preferentialRate(new BigDecimal("0.5"))
//////                        .finalRate(earthProduct.getBasicRate().add(new BigDecimal("0.5"))) // 기본금리 + 우대금리 (1.8 + 0.5 = 2.3%)
//////                        .build()
//////                );
//////                savingsAccountRepository.saveAll(savingsAccounts);
//////                log.info("고객 {}의 적금 계좌 {}개가 생성되었습니다.", user.getName(), savingsAccounts.size());
//////            } else {
//////                log.info("적금 계좌가 이미 존재합니다. 생성을 건너뜁니다.");
//////            }
//////        }
//////
//////        // 대출 계좌 생성
//////        if (loanAccountRepository.findByUser(user).isEmpty()) {
//////            // 대출 상품 조회
//////            List<LoanProduct> loanProducts = loanProductRepository.findAll();
//////            if (!loanProducts.isEmpty()) {
//////                LoanProduct loanProduct = loanProducts.get(0); // 첫 번째 상품 사용
//////
//////                // 계좌번호 중복 체크
//////                String loanAccountNumber = "220-123-456789";
//////                boolean loanAccountExists = loanAccountRepository.findByAccountNumber(loanAccountNumber).isPresent();
//////
//////                if (!loanAccountExists) {
//////                    List<LoanAccount> loanAccounts = Arrays.asList(
//////                        LoanAccount.builder()
//////                            .user(user)
//////                            .product(loanProduct)
//////                            .accountNumber(loanAccountNumber)
//////                            .accountName("하나 그린라이프 대출")
//////                            .loanAmount(30000000L)
//////                            .interestRate(new BigDecimal("5.2"))
//////                            .startDate(LocalDateTime.now().minusMonths(12).toLocalDate())
//////                            .maturityDate(LocalDateTime.now().plusMonths(48).toLocalDate())
//////                            .monthlyPayment(800000L)
//////                            .build()
//////                    );
//////                    loanAccountRepository.saveAll(loanAccounts);
//////                    log.info("고객 {}의 대출 계좌 {}개가 생성되었습니다.", user.getName(), loanAccounts.size());
//////                } else {
//////                    log.info("대출 계좌가 이미 존재합니다. 생성을 건너뜁니다.");
//////                }
//////            }
//////        }
//////
//////        // 투자 계좌 생성
//////        if (investmentAccountRepository.findByUser(user).isEmpty()) {
//////            // 투자 상품 조회
//////            List<InvestmentProduct> investmentProducts = investmentProductRepository.findAll();
//////            if (!investmentProducts.isEmpty()) {
//////                InvestmentProduct greenProduct = investmentProducts.get(0); // 첫 번째 상품 사용
//////
//////                // 계좌번호 중복 체크
//////                String investmentAccountNumber = "330-123-456789";
//////                boolean investmentAccountExists = investmentAccountRepository.findByAccountNumber(investmentAccountNumber).isPresent();
//////
//////                if (!investmentAccountExists) {
//////                    List<InvestmentAccount> investmentAccounts = Arrays.asList(
//////                        InvestmentAccount.builder()
//////                            .user(user)
//////                            .product(greenProduct)
//////                            .accountNumber(investmentAccountNumber)
//////                            .accountName("하나 그린라이프 투자")
//////                            .investmentAmount(3000000L)
//////                            .startDate(LocalDateTime.now().minusMonths(3).toLocalDate())
//////                            .build()
//////                    );
//////                    investmentAccountRepository.saveAll(investmentAccounts);
//////                    log.info("고객 {}의 투자 계좌 {}개가 생성되었습니다.", user.getName(), investmentAccounts.size());
//////                } else {
//////                    log.info("투자 계좌가 이미 존재합니다. 생성을 건너뜁니다.");
//////                }
//////            }
//////        }
//////
//////    }
//////}