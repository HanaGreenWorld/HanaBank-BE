package com.kopo.hanabank.integration.controller;

import com.kopo.hanabank.common.dto.ApiResponse;
import com.kopo.hanabank.integration.dto.BankCustomerInfoResponse;
import com.kopo.hanabank.integration.service.BankIntegrationService;
import com.kopo.hanabank.savings.dto.SavingsAccountCreateRequest;
import com.kopo.hanabank.savings.dto.SavingsAccountResponse;
import com.kopo.hanabank.deposit.dto.DemandDepositAccountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bank Integration API", description = "하나은행 그룹사 연동 API")
public class BankIntegrationController {

    private final BankIntegrationService bankIntegrationService;

    @PostMapping("/customer-info")
    @Operation(
        summary = "그룹사 고객 정보 조회",
        description = "다른 하나금융그룹 관계사에서 고객 정보를 요청할 때 사용하는 내부 API입니다."
    )
    public ResponseEntity<ApiResponse<BankCustomerInfoResponse>> getCustomerInfo(
            @RequestBody Map<String, String> request) {
        
        try {
            // customerInfoToken에서 CI 추출
            String customerInfoToken = request.get("customerInfoToken");
            String ci = extractCiFromCustomerToken(customerInfoToken);
            log.info("추출된 CI: {}", maskCi(ci));

            BankCustomerInfoResponse response = bankIntegrationService.getCustomerInfo(customerInfoToken, request.get("requestingService"));

            return ResponseEntity.ok(ApiResponse.success("고객 정보 조회가 완료되었습니다.", response));
            
        } catch (Exception e) {
            log.error("고객 정보 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("고객 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/electronic-receipts")
    @Operation(
        summary = "전자영수증 조회",
        description = "그룹 토큰으로 전자영수증 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getElectronicReceipts(
            @RequestBody Map<String, String> request) {
        
        try {
            String customerInfoToken = request.get("customerInfoToken");
            String ci = extractCiFromCustomerToken(customerInfoToken);
            log.info("추출된 CI: {}", maskCi(ci));

            List<Map<String, Object>> electronicReceipts = bankIntegrationService.getElectronicReceiptsByCI(customerInfoToken);

            return ResponseEntity.ok(ApiResponse.success("전자영수증 조회가 완료되었습니다.", electronicReceipts));
            
        } catch (Exception e) {
            log.error("전자영수증 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("전자영수증 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/deposit-accounts")
    @Operation(summary = "입출금 계좌 목록 조회", description = "고객의 입출금 계좌 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDepositAccounts(
            @RequestBody Map<String, String> request) {
        
        try {
            String customerInfoToken = request.get("customerInfoToken");
            String ci = extractCiFromCustomerToken(customerInfoToken);
            log.info("추출된 CI: {}", maskCi(ci));

            List<Map<String, Object>> depositAccounts = bankIntegrationService.getDepositAccountsByCi(ci);

            return ResponseEntity.ok(ApiResponse.success("입출금 계좌 목록 조회가 완료되었습니다.", depositAccounts));
            
        } catch (Exception e) {
            log.error("입출금 계좌 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("입출금 계좌 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/savings-accounts")
    @Operation(summary = "적금 계좌 생성", description = "새로운 적금 계좌를 생성합니다.")
    public ResponseEntity<ApiResponse<SavingsAccountResponse>> createSavingsAccount(
            @RequestBody SavingsAccountCreateRequest request) {
        
        try {
            String ci = extractCiFromCustomerToken(request.getCustomerInfoToken());

            SavingsAccountResponse response = bankIntegrationService.createSavingsAccountByToken(
                request.getProductId(),
                request.getPreferentialRate(),
                request.getApplicationAmount(),
                ci,
                request.getAutoTransferEnabled(),
                request.getTransferDay(),
                request.getMonthlyTransferAmount(),
                request.getWithdrawalAccountNumber(),
                request.getWithdrawalBankName()
            );
            
            return ResponseEntity.ok(ApiResponse.success("적금 계좌가 성공적으로 생성되었습니다.", response));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("적금 계좌 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/product-status")
    @Operation(summary = "고객 상품 현황 조회", description = "그룹사에서 고객의 상품 현황을 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getProductStatus(
            @RequestBody Map<String, String> request) {

        try {
            String customerInfoToken = request.get("customerInfoToken");
            String requestingService = (String) request.get("requestingService");
            String ci = extractCiFromCustomerToken(customerInfoToken);

            Object response = bankIntegrationService.getProductStatus(ci); // CI를 전화번호 대신 사용
            return ResponseEntity.ok(ApiResponse.success("상품 현황 조회가 완료되었습니다.", response));

        } catch (Exception e) {
            log.error("상품 현황 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("상품 현황 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/account-balance")
    @Operation(summary = "계좌 잔고 조회", description = "특정 계좌의 잔고를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getAccountBalance(
            @RequestBody Map<String, String> request) {

        try {
            String customerInfoToken = request.get("customerInfoToken");
            String accountNumber = request.get("accountNumber");
            String ci = extractCiFromCustomerToken(customerInfoToken);

            Object response = bankIntegrationService.getAccountBalance(ci); // CI를 전화번호 대신 사용
            return ResponseEntity.ok(ApiResponse.success("계좌 잔고 조회가 완료되었습니다.", response));

        } catch (Exception e) {
            log.error("계좌 잔고 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("계좌 잔고 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    private String extractCiFromCustomerToken(String customerInfoToken) {
        try {
            if (customerInfoToken == null || customerInfoToken.trim().isEmpty()) {
                log.error("고객 정보 토큰이 null이거나 비어있습니다: {}", customerInfoToken);
                throw new RuntimeException("고객 정보 토큰이 유효하지 않습니다.");
            }

            String ci = new String(Base64.getDecoder().decode(customerInfoToken));
            
            return ci;
            
        } catch (Exception e) {
            log.error("고객 정보 토큰에서 CI 추출 실패", e);
            throw new RuntimeException("CI 추출 실패: " + e.getMessage(), e);
        }
    }

    @PostMapping("/check-product-ownership")
    @Operation(summary = "상품 보유 여부 확인", description = "고객이 특정 상품을 보유하고 있는지 확인합니다.")
    public ResponseEntity<ApiResponse<Object>> checkProductOwnership(
            @RequestBody Map<String, Object> request) {

        try {
            Integer productId = (Integer) request.get("productId");
            String customerInfoToken = (String) request.get("customerInfoToken");
            
            if (productId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("productId가 필요합니다."));
            }
            
            if (customerInfoToken == null || customerInfoToken.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("customerInfoToken이 필요합니다."));
            }

            boolean hasProduct = bankIntegrationService.checkProductOwnership(customerInfoToken, productId.longValue());
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasProduct", hasProduct);
            response.put("productId", productId);
            response.put("customerInfoToken", customerInfoToken);

            log.info("상품 보유 여부 확인 완료 - 상품ID: {}, 보유여부: {}", productId, hasProduct);
            return ResponseEntity.ok(ApiResponse.success("상품 보유 여부 확인이 완료되었습니다.", response));

        } catch (Exception e) {
            log.error("상품 보유 여부 확인 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("상품 보유 여부 확인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    private String maskCustomerToken(String customerToken) {
        if (customerToken == null || customerToken.length() < 16) {
            return "****";
        }
        return customerToken.substring(0, 8) + "****" + customerToken.substring(customerToken.length() - 8);
    }

    private String maskCi(String ci) {
        if (ci == null || ci.length() < 8) {
            return "****";
        }
        return ci.substring(0, 4) + "****" + ci.substring(ci.length() - 4);
    }
}