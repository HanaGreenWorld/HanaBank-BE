package com.kopo.hanabank.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 그룹사 간 고객 정보 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupCustomerInfoRequest {

    /**
     * 그룹 내부 인증 토큰 (CI 대신 사용)
     */
    private String internalAuthToken;

    /**
     * 요청 서비스 (GREEN_WORLD, CARD 등)
     */
    private String requestingService;

    /**
     * 고객 동의 여부 확인 토큰
     */
    private String consentToken;

    /**
     * 요청 정보 타입 (BASIC, ACCOUNT, PRODUCT 등)
     */
    private String infoType;
}










