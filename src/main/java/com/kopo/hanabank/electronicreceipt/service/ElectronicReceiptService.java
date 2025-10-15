package com.kopo.hanabank.electronicreceipt.service;

import com.kopo.hanabank.electronicreceipt.domain.ElectronicReceipt;
import com.kopo.hanabank.electronicreceipt.repository.ElectronicReceiptRepository;
import com.kopo.hanabank.electronicreceipt.dto.ElectronicReceiptRequest;
import com.kopo.hanabank.electronicreceipt.dto.ElectronicReceiptResponse;
import com.kopo.hanabank.user.repository.UserRepository;
import com.kopo.hanabank.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ElectronicReceiptService {

    private final ElectronicReceiptRepository electronicReceiptRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${integration.green-world.url}")
    private String greenWorldUrl;

    public ElectronicReceiptResponse createElectronicReceipt(ElectronicReceiptRequest request) {
        try {

            String transactionId = request.getTransactionId();
            if (transactionId == null || transactionId.trim().isEmpty()) {
                transactionId = generateTransactionId();
            }

            // 중복 확인
            Optional<ElectronicReceipt> existingReceipt = electronicReceiptRepository
                .findByTransactionId(transactionId);
            
            if (existingReceipt.isPresent()) {
                log.warn("이미 존재하는 전자영수증입니다: transactionId={}", transactionId);
                return ElectronicReceiptResponse.from(existingReceipt.get());
            }

            User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: userId=" + request.getUserId()));

            ElectronicReceipt receipt = ElectronicReceipt.builder()
                .customerId(user.getId())
                .transactionId(transactionId)
                .transactionType(request.getTransactionType())
                .transactionAmount(request.getTransactionAmount())
                .branchName(request.getBranchName())
                .receiptDate(request.getReceiptDate())
                .build();

            ElectronicReceipt savedReceipt = electronicReceiptRepository.save(receipt);

            // 하나그린세상 사용자인지 확인
            boolean isGreenWorldUser = checkGreenWorldUser(user.getId().toString());
            if (isGreenWorldUser) {
                savedReceipt.setAsGreenWorldUser();
                electronicReceiptRepository.save(savedReceipt);
                
                // 웹훅 전송 (사용자 정보 포함)
                sendWebhookToGreenWorld(savedReceipt, user);
            }

            log.info("전자영수증 생성 완료: receiptId={}, transactionId={}, isGreenWorldUser={}", 
                savedReceipt.getReceiptId(), savedReceipt.getTransactionId(), isGreenWorldUser);

            return ElectronicReceiptResponse.from(savedReceipt);

        } catch (Exception e) {
            log.error("전자영수증 생성 실패: userId={}", request.getUserId(), e);
            throw new RuntimeException("전자영수증 생성에 실패했습니다.", e);
        }
    }

    private boolean checkGreenWorldUser(String customerId) {
        try {
            String url = greenWorldUrl + "/api/integration/user-verification/verify-by-ci";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("ci", customerId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return (Boolean) responseBody.getOrDefault("success", false);
            }
            
            return false;
            
        } catch (Exception e) {
            log.warn("하나그린세상 사용자 확인 실패: customerId={}", customerId, e);
            return false;
        }
    }

    private void sendWebhookToGreenWorld(ElectronicReceipt receipt, User user) {
        try {
            String url = greenWorldUrl + "/api/integration/webhook/electronic-receipt";
            
            Map<String, Object> webhookData = new HashMap<>();
            webhookData.put("ciToken", user.getCi()); // CI 토큰 포함
            webhookData.put("transactionId", receipt.getTransactionId());
            webhookData.put("transactionType", receipt.getTransactionType().name());
            webhookData.put("transactionAmount", receipt.getTransactionAmount());
            webhookData.put("branchName", receipt.getBranchName());
            webhookData.put("transactionDate", receipt.getReceiptDate());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(webhookData, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                receipt.markWebhookSent();
                electronicReceiptRepository.save(receipt);
                log.info("웹훅 전송 성공: transactionId={}", receipt.getTransactionId());
            } else {
                log.warn("웹훅 전송 실패: transactionId={}, status={}", 
                    receipt.getTransactionId(), response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("웹훅 전송 실패: transactionId={}", receipt.getTransactionId(), e);
        }
    }

    public List<ElectronicReceiptResponse> getElectronicReceiptsByCustomerId(Long customerId) {
        List<ElectronicReceipt> receipts = electronicReceiptRepository
            .findByCustomerIdOrderByReceiptDateDesc(customerId);
        
        return receipts.stream()
            .map(ElectronicReceiptResponse::from)
            .toList();
    }

    public void resendUnsentWebhooks() {
        List<ElectronicReceipt> unsentReceipts = electronicReceiptRepository.findUnsentWebhookReceipts();
        
        for (ElectronicReceipt receipt : unsentReceipts) {
            // 사용자 조회
            User user = userRepository.findById(receipt.getCustomerId())
                .orElse(null);
            
            if (user != null) {
                sendWebhookToGreenWorld(receipt, user);
            } else {
                log.warn("사용자를 찾을 수 없어 웹훅을 전송할 수 없습니다: customerId={}", receipt.getCustomerId());
            }
        }
        
        log.info("미전송 웹훅 재전송 완료: {}건", unsentReceipts.size());
    }

    private String generateTransactionId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "ER" + dateTime + random;
    }
}