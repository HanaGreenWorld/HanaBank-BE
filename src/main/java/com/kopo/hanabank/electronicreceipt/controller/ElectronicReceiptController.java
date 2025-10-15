package com.kopo.hanabank.electronicreceipt.controller;

import com.kopo.hanabank.electronicreceipt.dto.ElectronicReceiptRequest;
import com.kopo.hanabank.electronicreceipt.dto.ElectronicReceiptResponse;
import com.kopo.hanabank.electronicreceipt.service.ElectronicReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/electronic-receipts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Electronic Receipt", description = "전자영수증 관리 API")
public class ElectronicReceiptController {

    private final ElectronicReceiptService electronicReceiptService;

    @PostMapping
    @Operation(summary = "전자영수증 생성", description = "전자영수증을 생성하고 하나그린세상 사용자인 경우 웹훅을 전송합니다.")
    public ResponseEntity<ElectronicReceiptResponse> createElectronicReceipt(
            @RequestBody ElectronicReceiptRequest request) {
        try {
            ElectronicReceiptResponse response = electronicReceiptService.createElectronicReceipt(request);
            
            log.info("전자영수증 생성 완료: receiptId={}", response.getReceiptId());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("전자영수증 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "고객별 전자영수증 조회", description = "특정 고객의 전자영수증 목록을 조회합니다.")
    public ResponseEntity<List<ElectronicReceiptResponse>> getElectronicReceiptsByCustomerId(
            @PathVariable Long customerId) {
        try {
            List<ElectronicReceiptResponse> receipts = electronicReceiptService
                .getElectronicReceiptsByCustomerId(customerId);
            
            log.info("고객별 전자영수증 조회 완료: customerId={}, count={}", customerId, receipts.size());
            
            return ResponseEntity.ok(receipts);

        } catch (Exception e) {
            log.error("고객별 전자영수증 조회 실패: customerId={}, error={}", customerId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/resend-webhooks")
    @Operation(summary = "미전송 웹훅 재전송", description = "하나그린세상에 전송되지 않은 웹훅을 재전송합니다.")
    public ResponseEntity<String> resendUnsentWebhooks() {
        try {
            electronicReceiptService.resendUnsentWebhooks();
            
            log.info("미전송 웹훅 재전송 완료");
            
            return ResponseEntity.ok("미전송 웹훅 재전송이 완료되었습니다.");

        } catch (Exception e) {
            log.error("미전송 웹훅 재전송 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("미전송 웹훅 재전송에 실패했습니다.");
        }
    }
}
