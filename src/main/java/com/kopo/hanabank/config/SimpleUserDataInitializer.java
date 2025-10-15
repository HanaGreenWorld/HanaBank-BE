package com.kopo.hanabank.config;

import com.kopo.hanabank.user.domain.User;
import com.kopo.hanabank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleUserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 하나그린세상과 동일한 CI를 가진 사용자 생성
        String phoneNumber = "010-1234-5678";
        String ci = "CI_01012345678_123456"; // 하나그린세상과 동일한 CI
        
        // 이미 사용자가 있는지 확인
        if (userRepository.findByCi(ci).isEmpty()) {
            User user = User.builder()
                    .username("green_user")
                    .name("김그린")
                    .email("green@example.com")
                    .phoneNumber(phoneNumber)
                    .birthDate("1990-05-15")
                    .ci(ci)
                    .customerGrade("GOLD")
                    .isActive(true)
                    .build();
            
            User savedUser = userRepository.save(user);
            log.info("하나은행 테스트 사용자 생성 완료 - ID: {}, CI: {}", savedUser.getId(), ci);
        } else {
            log.info("하나은행 테스트 사용자가 이미 존재합니다 - CI: {}", ci);
        }
    }
}
