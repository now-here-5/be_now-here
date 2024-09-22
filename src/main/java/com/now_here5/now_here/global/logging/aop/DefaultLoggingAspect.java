package com.now_here5.now_here.global.logging.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DefaultLoggingAspect {

    private final HttpServletRequest request;

    @Around("execution(* com.now_here5.now_here.domain.*.*(..))")
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
            return result;
        } catch (Exception e) {
            log.error("❗Error occurred: ", e);
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("⏱️ Request to: {} completed in {} ms", joinPoint.getSignature().toShortString(), duration);
            log.info("📤 Response: {}", result);
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
