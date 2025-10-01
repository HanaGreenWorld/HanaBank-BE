package com.kopo.hanabank.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateRequest {

    @NotBlank(message = "사용자명은 필수입니다.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "유효한 전화번호 형식이 아닙니다.")
    private String phoneNumber;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "생년월일은 필수입니다.")
    @Pattern(regexp = "^[0-9]{8}$", message = "생년월일은 YYYYMMDD 형식이어야 합니다.")
    private String birthDate;

    private String address;
}












