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

/**
 * 하나은행 그룹사 연동 API 컨트롤러
 * 다른 하나금융그룹 관계사에서 고객 정보 요청 시 응답하는 내부 API
 */
@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bank Integration API", description = "하나은행 그룹사 연동 API")
public class BankIntegrationController {

    private final BankIntegrationService bankIntegrationService;

    /**
     * 그룹사 고객 정보 조회 API
     * 
     * @param request 그룹 고객 정보 요청
     * @return 하나은행 고객 정보
     */
    @PostMapping("/customer-info")
    @Operation(
        summary = "그룹사 고객 정보 조회",
        description = "다른 하나금융그룹 관계사에서 고객 정보를 요청할 때 사용하는 내부 API입니다. " +
                     "CI 대신 그룹 내부 토큰을 사용하여 안전하게 고객 정보를 제공합니다."
    )
    public ResponseEntity<ApiResponse<BankCustomerInfoResponse>> getCustomerInfo(
            @RequestBody Map<String, String> request) {
        
        try {
            log.info("=== 하나은행 그룹사 고객 정보 요청 시작 ===");
            log.info("요청 전체: {}", request);
            log.info("그룹고객토큰: {}", request.get("groupCustomerToken"));
            log.info("정보타입: {}", request.get("infoType"));
            log.info("요청서비스: {}", request.get("requestingService"));
            log.info("동의토큰: {}", request.get("consentToken"));
            
            // groupCustomerToken에서 Group Customer Token 추출
            String groupCustomerTokenRaw = request.get("groupCustomerToken");
            String groupCustomerToken = extractGroupCustomerToken(groupCustomerTokenRaw);
            log.info("추출된 Group Customer Token: {}", groupCustomerToken);
            
            log.info("🏦 하나은행 서비스 호출 시작");
            BankCustomerInfoResponse response = bankIntegrationService.getCustomerInfoByGroupToken(groupCustomerToken, request.get("infoType"));
            log.info("🏦 하나은행 서비스 호출 완료 - 상품수: {}", response != null ? response.getProducts().size() : 0);
            
            return ResponseEntity.ok(ApiResponse.success("고객 정보 조회가 완료되었습니다.", response));
            
        } catch (Exception e) {
            log.error("고객 정보 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("고객 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 적금 계좌 생성 API
     */
    @PostMapping("/savings-accounts")
    @Operation(summary = "적금 계좌 생성", description = "새로운 적금 계좌를 생성합니다.")
    public ResponseEntity<ApiResponse<SavingsAccountResponse>> createSavingsAccount(
            @RequestBody SavingsAccountCreateRequest request) {
        
        try {
            log.info("적금 계좌 생성 요청: {}", request);
            log.info("자동이체 설정: enabled={}, day={}, amount={}", 
                request.getAutoTransferEnabled(), request.getTransferDay(), request.getMonthlyTransferAmount());
            
            // customerInfoToken에서 전화번호 추출
            String phoneNumber = extractPhoneFromCustomerToken(request.getCustomerInfoToken());
            
            // BankIntegrationService를 통해 적금 계좌 생성 (자동이체 설정 포함)
            SavingsAccountResponse response = bankIntegrationService.createSavingsAccountByToken(
                request.getProductId(),
                request.getPreferentialRate(),
                request.getApplicationAmount(),
                phoneNumber,
                request.getAutoTransferEnabled(),
                request.getTransferDay(),
                request.getMonthlyTransferAmount(),
                request.getWithdrawalAccountNumber(),
                request.getWithdrawalBankName()
            );
            
            return ResponseEntity.ok(ApiResponse.success("적금 계좌가 성공적으로 생성되었습니다.", response));
            
        } catch (Exception e) {
            log.error("적금 계좌 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("적금 계좌 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 상품 현황 조회 API
     */
    @PostMapping("/product-status")
    @Operation(summary = "고객 상품 현황 조회", description = "그룹사에서 고객의 상품 현황을 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getProductStatus(
            @RequestBody Map<String, String> request) {

        try {
            String customerInfoToken = request.get("customerInfoToken");
            String requestingService = (String) request.get("requestingService");
            String phoneNumber = extractPhoneFromCustomerToken(customerInfoToken);

            Object response = bankIntegrationService.getProductStatus(phoneNumber);
            return ResponseEntity.ok(ApiResponse.success("상품 현황 조회가 완료되었습니다.", response));

        } catch (Exception e) {
            log.error("상품 현황 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("상품 현황 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 계좌 잔고 조회 API
     */
    @PostMapping("/account-balance")
    @Operation(summary = "계좌 잔고 조회", description = "특정 계좌의 잔고를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getAccountBalance(
            @RequestBody Map<String, String> request) {

        try {
            String customerInfoToken = request.get("customerInfoToken");
            String accountNumber = request.get("accountNumber");
            String phoneNumber = extractPhoneFromCustomerToken(customerInfoToken);

            Object response = bankIntegrationService.getAccountBalance(phoneNumber);
            return ResponseEntity.ok(ApiResponse.success("계좌 잔고 조회가 완료되었습니다.", response));

        } catch (Exception e) {
            log.error("계좌 잔고 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("계좌 잔고 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 고객 정보 토큰에서 전화번호 추출
     */
    private String extractPhoneFromCustomerToken(String customerInfoToken) {
        try {
            if (customerInfoToken == null || customerInfoToken.trim().isEmpty()) {
                log.error("고객 정보 토큰이 null이거나 비어있습니다: {}", customerInfoToken);
                throw new RuntimeException("고객 정보 토큰이 유효하지 않습니다.");
            }
            
            String decoded = new String(Base64.getDecoder().decode(customerInfoToken));
            log.info("디코딩된 토큰: {}", decoded);
            
            // 전화번호 추출 로직
            if (decoded.startsWith("CI_") && decoded.contains("_")) {
                String[] parts = decoded.split("_");
                if (parts.length >= 2) {
                    String phoneNumber = parts[1];
                    log.info("추출된 전화번호: {}", phoneNumber);
                    return phoneNumber;
                }
            }
            
            // 전화번호인 경우 Group Customer Token으로 변환
            if (decoded.matches("010-\\d{4}-\\d{4}")) {
                String phoneNumber = decoded.replace("-", "");
                return phoneNumber;
            }
            
            log.warn("토큰에서 올바른 전화번호를 찾을 수 없음: {}", decoded);
            throw new RuntimeException("유효하지 않은 고객 정보 토큰: " + decoded);
            
        } catch (Exception e) {
            log.error("고객 정보 토큰에서 전화번호 추출 실패", e);
            throw new RuntimeException("전화번호 추출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 고객 정보 토큰에서 Group Customer Token 추출
     */
    private String extractGroupCustomerToken(String customerInfoToken) {
        try {
            log.info("🔍 extractGroupCustomerToken 시작 - customerInfoToken: {}", customerInfoToken);
            log.info("🔍 customerInfoToken 타입: {}", customerInfoToken != null ? customerInfoToken.getClass().getSimpleName() : "null");
            log.info("🔍 customerInfoToken 길이: {}", customerInfoToken != null ? customerInfoToken.length() : "null");
            
            if (customerInfoToken == null || customerInfoToken.trim().isEmpty()) {
                log.error("고객 정보 토큰이 null이거나 비어있습니다: {}", customerInfoToken);
                throw new RuntimeException("고객 정보 토큰이 유효하지 않습니다.");
            }
            
            log.info("🔍 Base64 디코딩 시도: {}", customerInfoToken);
            String decoded = new String(Base64.getDecoder().decode(customerInfoToken));
            log.info("🔍 Base64 디코딩 결과: {}", decoded);
            
            // Group Customer Token 형식 검증 (CI_XXXXXXXXX_XXXXX_UNIFIED 또는 GCT_XXXXXXXXX_XXXXX_XXX)
            if (decoded.startsWith("CI_") || decoded.startsWith("GCT_")) {
                return decoded;
            }
            
            // 전화번호인 경우 Group Customer Token으로 변환
            if (decoded.matches("010-\\d{4}-\\d{4}")) {
                String phoneNumber = decoded.replace("-", "");
                return "GCT_" + phoneNumber + "_KIMHANA_001"; // 기본 Group Customer Token 생성
            }
            
            log.warn("토큰에서 올바른 Group Customer Token을 찾을 수 없음: {}", decoded);
            throw new RuntimeException("유효하지 않은 Group Customer Token: " + decoded);
            
        } catch (Exception e) {
            log.error("고객 정보 토큰에서 Group Customer Token 추출 실패", e);
            throw new RuntimeException("Group Customer Token 추출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 상품 보유 여부 확인
     */
    @PostMapping("/check-product-ownership")
    @Operation(summary = "상품 보유 여부 확인", description = "고객이 특정 상품을 보유하고 있는지 확인합니다.")
    public ResponseEntity<ApiResponse<Object>> checkProductOwnership(
            @RequestBody Map<String, Object> request) {

        try {
            log.info("상품 보유 여부 확인 요청: {}", request);
            
            Integer productId = (Integer) request.get("productId");
            String groupCustomerToken = (String) request.get("groupCustomerToken");
            
            if (productId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("productId가 필요합니다."));
            }
            
            if (groupCustomerToken == null || groupCustomerToken.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("groupCustomerToken이 필요합니다."));
            }

            // BankIntegrationService를 사용하여 실제 DB에서 확인
            boolean hasProduct = bankIntegrationService.checkProductOwnership(groupCustomerToken, productId.longValue());
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasProduct", hasProduct);
            response.put("productId", productId);
            response.put("groupCustomerToken", groupCustomerToken);

            log.info("상품 보유 여부 확인 완료 - 상품ID: {}, 보유여부: {}", productId, hasProduct);
            return ResponseEntity.ok(ApiResponse.success("상품 보유 여부 확인이 완료되었습니다.", response));

        } catch (Exception e) {
            log.error("상품 보유 여부 확인 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("상품 보유 여부 확인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}