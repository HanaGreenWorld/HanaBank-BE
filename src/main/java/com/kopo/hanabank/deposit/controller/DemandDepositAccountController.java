package com.kopo.hanabank.deposit.controller;

import com.kopo.hanabank.common.dto.ApiResponse;
import com.kopo.hanabank.common.exception.BusinessException;
import com.kopo.hanabank.deposit.dto.DemandDepositAccountCreateRequest;
import com.kopo.hanabank.deposit.dto.DemandDepositAccountResponse;
import com.kopo.hanabank.deposit.service.DemandDepositAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "입출금 계좌 관리", description = "입출금 계좌 관련 API")
@RestController
@RequestMapping("/deposit-accounts")
@RequiredArgsConstructor
@Slf4j
public class DemandDepositAccountController {

    private final DemandDepositAccountService demandDepositAccountService;

    @Operation(summary = "입출금 계좌 생성", description = "새로운 입출금 계좌를 생성합니다.")
    @PostMapping
    public ApiResponse<DemandDepositAccountResponse> createAccount(
            @Valid @RequestBody DemandDepositAccountCreateRequest request) {
        DemandDepositAccountResponse response = demandDepositAccountService.createAccount(request);
        return ApiResponse.success("입출금 계좌가 성공적으로 생성되었습니다.", response);
    }

    @Operation(summary = "사용자 입출금 계좌 목록 조회", description = "사용자의 모든 입출금 계좌를 조회합니다.")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<DemandDepositAccountResponse>> getUserAccounts(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        List<DemandDepositAccountResponse> responses = demandDepositAccountService.getUserAccounts(userId);
        return ApiResponse.success(responses);
    }

    @Operation(summary = "사용자 활성 입출금 계좌 목록 조회", description = "사용자의 활성 입출금 계좌만 조회합니다.")
    @GetMapping("/user/{userId}/active")
    public ApiResponse<List<DemandDepositAccountResponse>> getActiveUserAccounts(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        List<DemandDepositAccountResponse> responses = demandDepositAccountService.getActiveUserAccounts(userId);
        return ApiResponse.success(responses);
    }

    @Operation(summary = "계좌번호로 입출금 계좌 조회", description = "계좌번호로 입출금 계좌를 조회합니다.")
    @GetMapping("/{accountNumber}")
    public ApiResponse<DemandDepositAccountResponse> getAccount(
            @Parameter(description = "계좌번호") @PathVariable String accountNumber) {
        DemandDepositAccountResponse response = demandDepositAccountService.getAccountByNumber(accountNumber);
        return ApiResponse.success(response);
    }

    @Operation(summary = "입금", description = "입출금 계좌에 입금합니다.")
    @PostMapping("/{accountNumber}/deposit")
    public ApiResponse<DemandDepositAccountResponse> deposit(
            @Parameter(description = "계좌번호") @PathVariable String accountNumber,
            @RequestParam Long amount) {
        DemandDepositAccountResponse response = demandDepositAccountService.deposit(accountNumber, amount);
        return ApiResponse.success("입금이 성공적으로 처리되었습니다.", response);
    }

    @Operation(summary = "출금", description = "입출금 계좌에서 출금합니다.")
    @PostMapping("/{accountNumber}/withdraw")
    public ApiResponse<DemandDepositAccountResponse> withdraw(
            @Parameter(description = "계좌번호") @PathVariable String accountNumber,
            @RequestParam Long amount) {
        DemandDepositAccountResponse response = demandDepositAccountService.withdraw(accountNumber, amount);
        return ApiResponse.success("출금이 성공적으로 처리되었습니다.", response);
    }

    @Operation(summary = "내부 출금 (적금 가입용)", description = "하나그린월드에서 적금 가입 시 출금합니다.")
    @PostMapping("/{accountNumber}/withdraw-internal")
    public ResponseEntity<Map<String, Object>> withdrawInternal(
            @Parameter(description = "계좌번호") @PathVariable String accountNumber,
            @RequestBody Map<String, Object> requestBody) {

        try {
            // 계좌번호 검증
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                log.error("내부 출금 실패 - 계좌번호가 null 또는 빈 문자열: {}", accountNumber);
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "계좌번호가 유효하지 않습니다.");
                return ResponseEntity.status(400).body(result);
            }

            Long amount = Long.valueOf(requestBody.get("amount").toString());
            String transactionType = requestBody.get("transactionType").toString();

            log.info("내부 출금 요청 - 계좌번호: {}, 금액: {}, 거래유형: {}", accountNumber, amount, transactionType);

            DemandDepositAccountResponse response = demandDepositAccountService.withdraw(accountNumber, amount);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("accountNumber", response.getAccountNumber());
            result.put("remainingBalance", response.getBalance());

            return ResponseEntity.ok(result);

        } catch (BusinessException e) {
            log.error("내부 출금 실패 - 계좌번호: {}, 오류: {}", accountNumber, e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("errorCode", e.getErrorCode().getCode());

            return ResponseEntity.status(400).body(result);
        } catch (Exception e) {
            log.error("내부 출금 실패 - 계좌번호: {}, 오류: {}", accountNumber, e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "출금 처리 중 오류가 발생했습니다.");
            result.put("errorCode", "INTERNAL_ERROR");

            return ResponseEntity.status(500).body(result);
        }
    }

    @Operation(summary = "계좌 해지", description = "입출금 계좌를 해지합니다.")
    @DeleteMapping("/{accountNumber}")
    public ApiResponse<Void> closeAccount(
            @Parameter(description = "계좌번호") @PathVariable String accountNumber) {
        demandDepositAccountService.closeAccount(accountNumber);
        return ApiResponse.success("입출금 계좌가 성공적으로 해지되었습니다.", null);
    }
}
