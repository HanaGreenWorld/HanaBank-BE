package com.kopo.hanabank.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupCustomerInfoRequest {

    private String internalAuthToken;

    private String requestingService;

    private String consentToken;

    private String infoType;
}














