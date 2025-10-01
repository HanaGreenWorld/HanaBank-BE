package com.kopo.hanabank.common.config;

import com.kopo.hanabank.common.filter.InternalServiceAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final InternalServiceAuthFilter internalServiceAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health", "/health/status", "/actuator/**").permitAll() // Health check 허용
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger 허용
                .requestMatchers("/api/integration/**").permitAll() // 내부 API는 커스텀 필터에서 처리
                .anyRequest().permitAll() // 기타 요청은 모두 허용
            )
            .addFilterBefore(internalServiceAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}