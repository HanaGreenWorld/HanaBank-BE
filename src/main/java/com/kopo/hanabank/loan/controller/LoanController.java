package com.kopo.hanabank.loan.controller;

import com.kopo.hanabank.common.dto.ApiResponse;
import com.kopo.hanabank.loan.domain.LoanAccount;
import com.kopo.hanabank.loan.domain.LoanProduct;
import com.kopo.hanabank.loan.dto.LoanAccountResponse;
import com.kopo.hanabank.loan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "대출 관리", description = "대출 상품 및 계좌 관련 API")
@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;


    @Operation(summary = "대출 계좌 생성", description = "새로운 대출 계좌를 생성합니다.")
    @PostMapping("/accounts")
    public ApiResponse<LoanAccountResponse> createLoanAccount(@RequestParam Long userId, 
                                                    @RequestParam Long productId, 
                                                    @RequestParam Long loanAmount, 
                                                    @RequestParam Integer periodMonths) {
        LoanAccount account = loanService.createLoanAccount(userId, productId, loanAmount, periodMonths);
        return ApiResponse.success("대출 계좌가 성공적으로 생성되었습니다.", new LoanAccountResponse(account));
    }

    @Operation(summary = "사용자 대출 계좌 조회", description = "사용자의 모든 대출 계좌를 조회합니다.")
    @GetMapping("/accounts/user/{userId}")
    public ApiResponse<List<LoanAccountResponse>> getUserLoanAccounts(@PathVariable Long userId) {
        List<LoanAccount> accounts = loanService.getUserLoanAccounts(userId);
        List<LoanAccountResponse> responses = accounts.stream()
                .map(LoanAccountResponse::new)
                .toList();
        return ApiResponse.success(responses);
    }

    @Operation(summary = "대출 계좌 조회", description = "계좌번호로 대출 계좌를 조회합니다.")
    @GetMapping("/accounts/{accountNumber}")
    public ApiResponse<LoanAccountResponse> getLoanAccount(@PathVariable String accountNumber) {
        LoanAccount account = loanService.getLoanAccountByNumber(accountNumber);
        return ApiResponse.success(new LoanAccountResponse(account));
    }

    @Operation(summary = "대출 상환", description = "대출 계좌에서 상환합니다.")
    @PostMapping("/accounts/repay")
    public ApiResponse<LoanAccountResponse> repayLoan(@RequestParam String accountNumber, 
                                            @RequestParam Long amount) {
        LoanAccount account = loanService.repayLoan(accountNumber, amount);
        return ApiResponse.success("상환이 성공적으로 처리되었습니다.", new LoanAccountResponse(account));
    }

    @Operation(summary = "대출 계좌 해지", description = "대출 계좌를 해지합니다.")
    @DeleteMapping("/accounts/{accountNumber}")
    public ApiResponse<Void> closeLoanAccount(@PathVariable String accountNumber) {
        loanService.closeLoanAccount(accountNumber);
        return ApiResponse.success("대출 계좌가 성공적으로 해지되었습니다.", null);
    }
}


