package com.kopo.hanabank.user.service;

import com.kopo.hanabank.common.exception.BusinessException;
import com.kopo.hanabank.common.exception.ErrorCode;
import com.kopo.hanabank.user.domain.User;
import com.kopo.hanabank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User createUser(String username, String email, String phoneNumber, 
                          String name, String birthDate, String address) {
        
        // 중복 체크
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .phoneNumber(phoneNumber)
                .name(name)
                .birthDate(birthDate)
                .address(address)
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, String name, String address) {
        User user = getUserById(id);
        user.updateUserInfo(name, address);
        return user;
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.deactivate();
    }
}












