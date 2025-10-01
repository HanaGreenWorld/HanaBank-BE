package com.kopo.hanabank.savings.controller;

import com.kopo.hanabank.common.dto.ApiResponse;
import com.kopo.hanabank.savings.domain.SavingsAccount;
import com.kopo.hanabank.savings.domain.SavingsProduct;
import com.kopo.hanabank.savings.dto.SavingsAccountCreateRequest;
import com.kopo.hanabank.savings.dto.SavingsAccountResponse;
import com.kopo.hanabank.savings.dto.SavingsTransactionRequest;
import com.kopo.hanabank.savings.service.SavingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "적금 관리", description = "적금 상품 및 계좌 관련 API")
@RestController
@RequestMapping("/savings")
@RequiredArgsConstructor
public class SavingsController {

    private final SavingsService savingsService;


    @Operation(summary = "적금 계좌 생성", description = "새로운 적금 계좌를 생성합니다.")
    @PostMapping("/accounts")
    public ApiResponse<SavingsAccountResponse> createSavingsAccount(@Valid @RequestBody SavingsAccountCreateRequest request) {
        SavingsAccount account = savingsService.createSavingsAccount(
                request.getUserId(),
                request.getProductId(),
                request.getPreferentialRate(),
                request.getApplicationAmount()
        );
        return ApiResponse.success("적금 계좌가 성공적으로 생성되었습니다.", new SavingsAccountResponse(account));
    }

    @Operation(summary = "사용자 적금 계좌 조회", description = "사용자의 모든 적금 계좌를 조회합니다.")
    @GetMapping("/accounts/user/{userId}")
    public ApiResponse<List<SavingsAccountResponse>> getUserSavingsAccounts(@PathVariable Long userId) {
        List<SavingsAccount> accounts = savingsService.getUserSavingsAccounts(userId);
        List<SavingsAccountResponse> responses = accounts.stream()
                .map(SavingsAccountResponse::new)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @Operation(summary = "적금 계좌 조회", description = "계좌번호로 적금 계좌를 조회합니다.")
    @GetMapping("/accounts/{accountNumber}")
    public ApiResponse<SavingsAccountResponse> getSavingsAccount(@PathVariable String accountNumber) {
        SavingsAccount account = savingsService.getSavingsAccountByNumber(accountNumber);
        return ApiResponse.success(new SavingsAccountResponse(account));
    }

    @Operation(summary = "적금 입금", description = "적금 계좌에 입금합니다.")
    @PostMapping("/accounts/deposit")
    public ApiResponse<SavingsAccountResponse> depositToSavings(@Valid @RequestBody SavingsTransactionRequest request) {
        SavingsAccount account = savingsService.depositToSavings(request.getAccountNumber(), request.getAmount());
        return ApiResponse.success("입금이 성공적으로 처리되었습니다.", new SavingsAccountResponse(account));
    }

    @Operation(summary = "적금 출금", description = "적금 계좌에서 출금합니다.")
    @PostMapping("/accounts/withdraw")
    public ApiResponse<SavingsAccountResponse> withdrawFromSavings(@Valid @RequestBody SavingsTransactionRequest request) {
        SavingsAccount account = savingsService.withdrawFromSavings(request.getAccountNumber(), request.getAmount());
        return ApiResponse.success("출금이 성공적으로 처리되었습니다.", new SavingsAccountResponse(account));
    }

    @Operation(summary = "적금 계좌 해지", description = "적금 계좌를 해지합니다.")
    @DeleteMapping("/accounts/{accountNumber}")
    public ApiResponse<Void> closeSavingsAccount(@PathVariable String accountNumber) {
        savingsService.closeSavingsAccount(accountNumber);
        return ApiResponse.success("적금 계좌가 성공적으로 해지되었습니다.", null);
    }
}


