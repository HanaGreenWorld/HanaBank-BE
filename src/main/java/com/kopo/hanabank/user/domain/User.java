package com.kopo.hanabank.user.domain;

import com.kopo.hanabank.common.domain.DateTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private String birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "ci", unique = true)
    private String ci;

    @Column(name = "group_customer_token", unique = true)
    private String groupCustomerToken;
    
    @Column(name = "customer_grade")
    private String customerGrade;

    @Builder
    public User(Long id, String username, String email, String phoneNumber, String name, 
                String birthDate, String address, String customerGrade, Boolean isActive, String ci, String groupCustomerToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.customerGrade = customerGrade;
        this.isActive = isActive != null ? isActive : true;
        this.ci = ci;
        this.groupCustomerToken = groupCustomerToken;
    }

    public void updateUserInfo(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getCi() {
        return this.ci;
    }

    public String getGroupCustomerToken() {
        return this.groupCustomerToken;
    }
}




