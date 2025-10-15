package com.kopo.hanabank.electronicreceipt.domain;

/**
 * 전자영수증 거래 유형 enum
 */
public enum TransactionType {
    DEPOSIT("입금 확인증", "입금"),
    PAYMENT("지급 확인증", "지급"),
    MATURITY_RENEWAL("만기갱신 확인증", "만기갱신"),
    CANCELLATION("해지 확인증", "해지");

    private final String displayName;
    private final String shortName;

    TransactionType(String displayName, String shortName) {
        this.displayName = displayName;
        this.shortName = shortName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }
}

