package com.kopo.hanabank.config;

import com.kopo.hanabank.savings.domain.SavingsProduct;
import com.kopo.hanabank.savings.repository.SavingsProductRepository;
import com.kopo.hanabank.loan.domain.LoanProduct;
import com.kopo.hanabank.loan.repository.LoanProductRepository;
import com.kopo.hanabank.investment.domain.InvestmentProduct;
import com.kopo.hanabank.investment.repository.InvestmentProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankProductDataInitializer implements CommandLineRunner {

    private final SavingsProductRepository savingsProductRepository;
    private final LoanProductRepository loanProductRepository;
    private final InvestmentProductRepository investmentProductRepository;

    @Override
    public void run(String... args) throws Exception {
        if (savingsProductRepository.count() == 0) {
            log.info("은행 상품 더미데이터 초기화 시작...");
            initializeSavingsProducts();
            log.info("적금 상품 더미데이터 초기화 완료!");
        } else {
            log.info("적금 상품 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
        }

        if (loanProductRepository.count() == 0) {
            log.info("대출 상품 더미데이터 초기화 시작...");
            initializeLoanProducts();
            log.info("대출 상품 더미데이터 초기화 완료!");
        } else {
            log.info("대출 상품 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
        }

        if (investmentProductRepository.count() == 0) {
            log.info("투자 상품 더미데이터 초기화 시작...");
            initializeInvestmentProducts();
            log.info("투자 상품 더미데이터 초기화 완료!");
        } else {
            log.info("투자 상품 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
        }
    }

    private void initializeSavingsProducts() {
        List<SavingsProduct> savingsProducts = Arrays.asList(
            // 지구사랑적금
            SavingsProduct.builder()
                .productName("지구 사랑 적금")
                .productType("EARTH_SAVINGS")
                .description("지구를 사랑하는 마음으로 시작하는 친환경 적금으로, 환경보호 활동에 참여하면 추가 금리를 받을 수 있습니다.")
                .basicRate(new BigDecimal("1.8"))
                .maxRate(new BigDecimal("3.5"))
                .preferentialRate(new BigDecimal("1.7"))
                .minAmount(100000L)
                .maxAmount(10000000L)
                .termMonths(24)
                .depositType(SavingsProduct.DepositType.FREE_SAVINGS)
                .interestPaymentType("만기일시지급식")
                .isActive(true)
                .build(),

            // 그린라이프 적금
            SavingsProduct.builder()
                .productName("하나 그린라이프 적금")
                .productType("GREEN_SAVINGS")
                .description("친환경 라이프스타일을 위한 적금으로, 매월 친환경 활동을 실천하면 추가 금리를 받을 수 있습니다.")
                .basicRate(new BigDecimal("2.5"))
                .maxRate(new BigDecimal("4.0"))
                .preferentialRate(new BigDecimal("1.5"))
                .minAmount(100000L)
                .maxAmount(5000000L)
                .termMonths(12)
                .depositType(SavingsProduct.DepositType.FREE_SAVINGS)
                .interestPaymentType("만기일시지급식")
                .isActive(true)
                .build(),

            // 원큐씨앗 적금
            SavingsProduct.builder()
                .productName("하나 원큐씨앗 적금")
                .productType("ECO_SEED_SAVINGS")
                .description("원큐씨앗 적립과 함께하는 적금으로, 매월 원큐씨앗을 적립하면 추가 금리를 받을 수 있습니다.")
                .basicRate(new BigDecimal("2.8"))
                .maxRate(new BigDecimal("4.3"))
                .preferentialRate(new BigDecimal("1.5"))
                .minAmount(50000L)
                .maxAmount(3000000L)
                .termMonths(24)
                .depositType(SavingsProduct.DepositType.INSTALLMENT_SAVINGS)
                .interestPaymentType("월복리식")
                .isActive(true)
                .build(),

            // 제로웨이스트 적금
            SavingsProduct.builder()
                .productName("하나 제로웨이스트 적금")
                .productType("ZERO_WASTE_SAVINGS")
                .description("제로웨이스트 라이프를 위한 적금으로, 리필샵 이용 시 추가 금리를 받을 수 있습니다.")
                .basicRate(new BigDecimal("2.2"))
                .maxRate(new BigDecimal("3.7"))
                .preferentialRate(new BigDecimal("1.5"))
                .minAmount(200000L)
                .maxAmount(10000000L)
                .termMonths(36)
                .depositType(SavingsProduct.DepositType.FREE_SAVINGS)
                .interestPaymentType("만기일시지급식")
                .isActive(true)
                .build()
        );

        savingsProductRepository.saveAll(savingsProducts);
        log.info("총 {}개의 적금 상품이 생성되었습니다.", savingsProducts.size());
    }

    private void initializeLoanProducts() {
        List<LoanProduct> loanProducts = Arrays.asList(
            // 그린라이프 대출
            LoanProduct.builder()
                .productName("하나 그린라이프 대출")
                .productType("GREEN_LOAN")
                .description("친환경 라이프스타일을 위한 대출로, 전기차 구매, 친환경 리모델링 시 우대금리를 제공합니다.")
                .baseRate(new BigDecimal("4.5"))
                .maxRate(new BigDecimal("6.0"))
                .preferentialRate(new BigDecimal("1.5"))
                .minAmount(1000000L)
                .maxAmount(100000000L)
                .periodMonths(60)
                .loanType(LoanProduct.LoanType.PERSONAL_LOAN)
                .isActive(true)
                .build(),

            // 원큐씨앗 대출
            LoanProduct.builder()
                .productName("하나 원큐씨앗 대출")
                .productType("ECO_SEED_LOAN")
                .description("원큐씨앗 적립과 함께하는 대출로, 원큐씨앗 적립 시 추가 우대금리를 제공합니다.")
                .baseRate(new BigDecimal("4.2"))
                .maxRate(new BigDecimal("5.7"))
                .preferentialRate(new BigDecimal("1.3"))
                .minAmount(500000L)
                .maxAmount(50000000L)
                .periodMonths(36)
                .loanType(LoanProduct.LoanType.PERSONAL_LOAN)
                .isActive(true)
                .build(),

            // 제로웨이스트 대출
            LoanProduct.builder()
                .productName("하나 제로웨이스트 대출")
                .productType("ZERO_WASTE_LOAN")
                .description("제로웨이스트 라이프를 위한 대출로, 친환경 제품 구매 시 우대금리를 제공합니다.")
                .baseRate(new BigDecimal("4.8"))
                .maxRate(new BigDecimal("6.3"))
                .preferentialRate(new BigDecimal("1.5"))
                .minAmount(2000000L)
                .maxAmount(200000000L)
                .periodMonths(84)
                .loanType(LoanProduct.LoanType.MORTGAGE)
                .isActive(true)
                .build()
        );

        loanProductRepository.saveAll(loanProducts);
        log.info("총 {}개의 대출 상품이 생성되었습니다.", loanProducts.size());
    }

    private void initializeInvestmentProducts() {
        List<InvestmentProduct> investmentProducts = Arrays.asList(
            // 그린라이프 펀드
            InvestmentProduct.builder()
                .name("하나 그린라이프 펀드")
                .productType(InvestmentProduct.ProductType.STOCK_FUND)
                .description("친환경 기업에 투자하는 펀드로, ESG 경영 기업에 집중 투자합니다.")
                .riskLevel(2)
                .expectedReturnRate(new BigDecimal("6.5"))
                .minInvestmentAmount(100000L)
                .managementFee(new BigDecimal("1.2"))
                .isActive(true)
                .build(),

            // 원큐씨앗 펀드
            InvestmentProduct.builder()
                .name("하나 원큐씨앗 펀드")
                .productType(InvestmentProduct.ProductType.STOCK_FUND)
                .description("원큐씨앗 적립과 함께하는 펀드로, 친환경 에너지 기업에 투자합니다.")
                .riskLevel(3)
                .expectedReturnRate(new BigDecimal("8.0"))
                .minInvestmentAmount(50000L)
                .managementFee(new BigDecimal("1.5"))
                .isActive(true)
                .build(),

            // 제로웨이스트 펀드
            InvestmentProduct.builder()
                .name("하나 제로웨이스트 펀드")
                .productType(InvestmentProduct.ProductType.MIXED_FUND)
                .description("제로웨이스트 관련 기업에 투자하는 펀드로, 순환경제 기업에 집중 투자합니다.")
                .riskLevel(1)
                .expectedReturnRate(new BigDecimal("4.5"))
                .minInvestmentAmount(200000L)
                .managementFee(new BigDecimal("0.8"))
                .isActive(true)
                .build()
        );

        investmentProductRepository.saveAll(investmentProducts);
        log.info("총 {}개의 투자 상품이 생성되었습니다.", investmentProducts.size());
    }
}
