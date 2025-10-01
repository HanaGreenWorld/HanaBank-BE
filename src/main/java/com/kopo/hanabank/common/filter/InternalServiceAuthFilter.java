package com.kopo.hanabank.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Component
public class InternalServiceAuthFilter extends OncePerRequestFilter {
    
    @Value("${internal.service.secret}")
    private String secret;

    @Value("${internal.auth.header}")
    private String authHeader;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        log.info("=== 하나은행 내부 서비스 인증 필터 시작 ===");
        log.info("요청 URI: {}", requestURI);
        log.info("요청 메서드: {}", method);
        log.info("요청 헤더: {}", request.getHeaderNames());
        
        // 내부 API 요청인지 확인
        if (requestURI.startsWith("/api/integration/")) {
            log.info("내부 API 요청 감지");
            String internalAuth = request.getHeader(authHeader);
            log.info("내부 인증 헤더: {}", internalAuth);
            
            if (internalAuth == null || !validateInternalAuth(internalAuth)) {
                log.error("내부 서비스 인증 실패: URI={}, Auth={}", requestURI, internalAuth);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Unauthorized internal service access\"}");
                return;
            }
            
            log.info("내부 서비스 인증 성공: URI={}", requestURI);
        } else {
            log.info("일반 API 요청 - 인증 생략");
        }
        
        log.info("필터 체인 진행");
        filterChain.doFilter(request, response);
    }
    
    private boolean validateInternalAuth(String authHeader) {
        try {
            log.info("=== 토큰 검증 시작 ===");
            log.info("받은 토큰: {}", authHeader);
            log.info("기대하는 시크릿: {}", secret);
            
            // Base64 디코딩하여 시크릿 확인
            String decoded = new String(Base64.getDecoder().decode(authHeader));
            log.info("디코딩된 토큰: {}", decoded);
            
            boolean isValid = secret.equals(decoded);
            log.info("토큰 검증 결과: {}", isValid);
            
            return isValid;
        } catch (Exception e) {
            log.error("내부 서비스 인증 토큰 파싱 실패", e);
            return false;
        }
    }
}
