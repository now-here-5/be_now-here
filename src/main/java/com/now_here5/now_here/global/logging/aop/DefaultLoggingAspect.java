package com.now_here5.now_here.global.logging.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

@Aspect
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultLoggingAspect {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    @Around("execution(* com.now_here5.now_here.domain.member.controller.MemberController.*(..)) || " +
            "execution(* com.now_here5.now_here.domain.member.controller.MemberInfoController.*(..)) || " +
            "execution(* com.now_here5.now_here.domain.event.controller.EventController.*(..)) || " +
            "execution(* com.now_here5.now_here.domain.interaction.controller.InteractionController.*(..)) || " +
            "execution(* com.now_here5.now_here.domain.matching.controller.MatchingController.*(..))")
    public Object logAPIRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        // 요청 정보 로깅
        String httpMethod = request.getMethod();
        String requestUrl = request.getRequestURI();
        String clientIp = getClientIp();
        log.info("🔍 Incoming Request - Method: {}, URL: {}, Client IP: {}", httpMethod, requestUrl, clientIp);

        // 실행 시간 측정
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();  // 실제 메서드 실행
        } catch (Exception e) {
            logError(e, requestUrl, clientIp);
            throw e;  // 예외를 다시 던져서 처리
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("⏱️ Request to: {} completed in {} ms", joinPoint.getSignature().toShortString(), duration);
            logResponse(result);
        }
        return result;
    }

    // 클라이언트의 IP 주소를 가져오는 메서드
    private String getClientIp() {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    // 에러 로깅 메서드
    private void logError(Exception e, String requestUrl, String clientIp) {
        log.error("❗ Error occurred while processing request to {} from IP {}: {}", requestUrl, clientIp, e.getMessage(), e);
    }

    // 응답 로깅 메서드
    private void logResponse(Object result) {
        if (result == null) {
            log.info("📤 Response: No response (null)");
            return;
        }
        try {
            log.info("📤 Response: {}", objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.error("Error serializing response to JSON: {}", e.getMessage());
        }
    }
}
