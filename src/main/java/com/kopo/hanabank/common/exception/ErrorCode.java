package com.kopo.hanabank.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 사용자 관련
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("U002", "이미 존재하는 사용자입니다."),
    INVALID_USER_INFO("U003", "유효하지 않은 사용자 정보입니다."),
    
    // 적금 관련
    SAVINGS_PRODUCT_NOT_FOUND("S001", "적금 상품을 찾을 수 없습니다."),
    SAVINGS_ACCOUNT_NOT_FOUND("S002", "적금 계좌를 찾을 수 없습니다."),
    INVALID_SAVINGS_AMOUNT("S003", "유효하지 않은 적금 금액입니다."),
    SAVINGS_ACCOUNT_ALREADY_EXISTS("S004", "이미 존재하는 적금 계좌입니다."),
    SAVINGS_ACCOUNT_TRANSACTION_FAILED("S005", "적금 계좌 거래 처리에 실패했습니다."),
    SAVINGS_ACCOUNT_HAS_BALANCE("S006", "적금 계좌에 잔고가 있어 해지할 수 없습니다."),

    // 입출금 계좌 관련
    DEMAND_DEPOSIT_ACCOUNT_NOT_FOUND("D001", "입출금 계좌를 찾을 수 없습니다."),
    DEMAND_DEPOSIT_ACCOUNT_ALREADY_EXISTS("D002", "이미 존재하는 입출금 계좌입니다."),
    INVALID_DEMAND_DEPOSIT_AMOUNT("D003", "유효하지 않은 입출금 금액입니다."),
    INSUFFICIENT_BALANCE("D004", "잔액이 부족합니다."),
    
    // 투자 관련
    INVESTMENT_PRODUCT_NOT_FOUND("I001", "투자 상품을 찾을 수 없습니다."),
    INVESTMENT_ACCOUNT_NOT_FOUND("I002", "투자 계좌를 찾을 수 없습니다."),
    INVALID_INVESTMENT_AMOUNT("I003", "유효하지 않은 투자 금액입니다."),
    
    // 대출 관련
    LOAN_PRODUCT_NOT_FOUND("L001", "대출 상품을 찾을 수 없습니다."),
    LOAN_ACCOUNT_NOT_FOUND("L002", "대출 계좌를 찾을 수 없습니다."),
    INVALID_LOAN_AMOUNT("L003", "유효하지 않은 대출 금액입니다."),
    LOAN_APPLICATION_REJECTED("L004", "대출 신청이 거절되었습니다."),
    
    // 공통
    INTERNAL_SERVER_ERROR("C001", "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST("C002", "유효하지 않은 요청입니다."),
    UNAUTHORIZED("C003", "인증이 필요합니다."),
    FORBIDDEN("C004", "접근 권한이 없습니다."),
    INVALID_AMOUNT("C005", "유효하지 않은 금액입니다.");

    private final String code;
    private final String message;
}