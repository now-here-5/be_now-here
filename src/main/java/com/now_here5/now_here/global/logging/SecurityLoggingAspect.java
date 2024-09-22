package com.now_here5.now_here.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityLoggingAspect {

    private final HttpServletRequest request;

    // MemberAuthController 클래스에서 발생하는 로그인/로그아웃 이벤트 로깅
    @After("execution(* com.now_here5.now_here.domain.member.controller.MemberAuthController.*(..))")
    public void logMemberAuthControllerEvents(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUsername();
        String clientIp = getClientIp();

        if ("login".equals(methodName)) {
            log.info("🔑 Login Event - User: {}, IP: {}, Method: {}", username, clientIp, methodName);
        } else if ("logout".equals(methodName)) {
            log.info("🔓 Logout Event - User: {}, IP: {}, Method: {}", username, clientIp, methodName);
        } else {
            log.info("🔒 Security Event in MemberAuthController - User: {}, IP: {}, Method: {}", username, clientIp, methodName);
        }
    }

    // 회원가입 및 탈퇴 이벤트에 대해 로깅
    @After("execution(* com.now_here5.now_here.domain.member.controller.MemberAccountController.deactivateMember(..)) ||" +
            " execution(* com.now_here5.now_here.domain.member.controller.MemberAccountController.registerMember(..))")
    public void logMemberAccountEvents(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String clientIp = getClientIp();

        if ("deactivateMember".equals(methodName)) {
            String username = getCurrentUsername();
            log.info("🚫 Member Deactivation Event - User: {}, IP: {}, Method: {}", username, clientIp, methodName);
        } else if ("registerMember".equals(methodName)) {
            Object[] args = joinPoint.getArgs();
            String newMemberEmail = (String) args[0]; // 첫 번째 파라미터가 이메일이라 가정
            log.info("✅ New Member Registration Event - Email: {}, IP: {}, Method: {}", newMemberEmail, clientIp, methodName);
        }
    }

    // 현재 인증된 유저의 이름을 가져오는 메서드 (로그인/로그아웃 시 사용)
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : "Anonymous";
    }

    // 클라이언트의 IP 주소를 가져오는 메서드
    private String getClientIp() {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
