package com.kopo.hanabank.user.dto;

import com.kopo.hanabank.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String name;
    private String birthDate;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.name = user.getName();
        this.birthDate = user.getBirthDate();
        this.address = user.getAddress();
        this.isActive = user.getIsActive();
        this.createdAt = user.getCreatedAt();
    }
}

