package com.now_here5.now_here.global.logging.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now_here5.now_here.global.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityLoggingAspect {

    private final HttpServletRequest request;
    private final AuthUtil authUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // MemberAuthController 클래스에서 발생하는 로그인/로그아웃 이벤트 로깅
    @After("execution(* com.now_here5.now_here.domain.member.controller.MemberAuthController.*(..))")
    public void logMemberAuthControllerEvents(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUsername(joinPoint.getArgs());
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
            String username = getCurrentUsername(joinPoint.getArgs());
            log.info("🚫 Member Deactivation Event - User: {}, IP: {}, Method: {}", username, clientIp, methodName);
        } else if ("registerMember".equals(methodName)) {
            log.info("✅ New Member Registration Event : IP: {}, Method: {}", clientIp, methodName);
        }
    }

    // 현재 인증된 유저의 이름을 가져오는 메서드 (로그인/로그아웃 시 사용)
    private String getCurrentUsername(Object[] args) {
        try {
            return args[0].toString();
        } catch (ArrayIndexOutOfBoundsException e) {
            return authUtil.getMemberByAuthentication().getMemberId().toString();
        } catch (Exception e) {
            return "Anonymous";
        }
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
