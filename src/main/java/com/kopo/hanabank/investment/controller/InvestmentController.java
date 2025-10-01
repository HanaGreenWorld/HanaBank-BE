package com.kopo.hanabank.investment.controller;

import com.kopo.hanabank.common.dto.ApiResponse;
import com.kopo.hanabank.investment.domain.InvestmentAccount;
import com.kopo.hanabank.investment.domain.InvestmentProduct;
import com.kopo.hanabank.investment.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "투자 관리", description = "투자 상품 및 계좌 관련 API")
@RestController
@RequestMapping("/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @Operation(summary = "투자 상품 목록 조회", description = "모든 활성 투자 상품을 조회합니다.")
    @GetMapping("/products")
    public ApiResponse<List<InvestmentProduct>> getAllInvestmentProducts() {
        List<InvestmentProduct> products = investmentService.getAllInvestmentProducts();
        return ApiResponse.success(products);
    }

    @Operation(summary = "투자 상품 조회", description = "ID로 투자 상품을 조회합니다.")
    @GetMapping("/products/{id}")
    public ApiResponse<InvestmentProduct> getInvestmentProduct(@PathVariable Long id) {
        InvestmentProduct product = investmentService.getInvestmentProductById(id);
        return ApiResponse.success(product);
    }

    @Operation(summary = "투자 계좌 생성", description = "새로운 투자 계좌를 생성합니다.")
    @PostMapping("/accounts")
    public ApiResponse<InvestmentAccount> createInvestmentAccount(@RequestParam Long userId, 
                                                                 @RequestParam Long productId, 
                                                                 @RequestParam Long investmentAmount) {
        InvestmentAccount account = investmentService.createInvestmentAccount(userId, productId, investmentAmount);
        return ApiResponse.success("투자 계좌가 성공적으로 생성되었습니다.", account);
    }

    @Operation(summary = "사용자 투자 계좌 조회", description = "사용자의 모든 투자 계좌를 조회합니다.")
    @GetMapping("/accounts/user/{userId}")
    public ApiResponse<List<InvestmentAccount>> getUserInvestmentAccounts(@PathVariable Long userId) {
        List<InvestmentAccount> accounts = investmentService.getUserInvestmentAccounts(userId);
        return ApiResponse.success(accounts);
    }

    @Operation(summary = "투자 계좌 조회", description = "계좌번호로 투자 계좌를 조회합니다.")
    @GetMapping("/accounts/{accountNumber}")
    public ApiResponse<InvestmentAccount> getInvestmentAccount(@PathVariable String accountNumber) {
        InvestmentAccount account = investmentService.getInvestmentAccountByNumber(accountNumber);
        return ApiResponse.success(account);
    }

    @Operation(summary = "투자", description = "투자 계좌에 추가 투자합니다.")
    @PostMapping("/accounts/invest")
    public ApiResponse<InvestmentAccount> invest(@RequestParam String accountNumber, 
                                               @RequestParam Long amount) {
        InvestmentAccount account = investmentService.invest(accountNumber, amount);
        return ApiResponse.success("투자가 성공적으로 처리되었습니다.", account);
    }

    @Operation(summary = "환매", description = "투자 계좌에서 환매합니다.")
    @PostMapping("/accounts/redeem")
    public ApiResponse<InvestmentAccount> redeem(@RequestParam String accountNumber, 
                                               @RequestParam Long amount) {
        InvestmentAccount account = investmentService.redeem(accountNumber, amount);
        return ApiResponse.success("환매가 성공적으로 처리되었습니다.", account);
    }

    @Operation(summary = "투자 계좌 해지", description = "투자 계좌를 해지합니다.")
    @DeleteMapping("/accounts/{accountNumber}")
    public ApiResponse<Void> closeInvestmentAccount(@PathVariable String accountNumber) {
        investmentService.closeInvestmentAccount(accountNumber);
        return ApiResponse.success("투자 계좌가 성공적으로 해지되었습니다.", null);
    }
}












