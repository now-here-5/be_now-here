package com.now_here5.now_here.global.logging.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExternalApiLoggingAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // @ExternalApiLogging 어노테이션이 적용된 메소드에만 AOP 적용
    @Around("@annotation(com.now_here5.now_here.global.logging.annotation.ExternalApiLogging)")
    public Object logExternalApiCall(ProceedingJoinPoint joinPoint) throws Throwable {

        // 메서드 이름과 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        String apiUrl = null;
        String requestParams = null;

        if (args.length > 0) {
            apiUrl = objectMapper.writeValueAsString(args[0]); // API URL
            if (args.length > 1) {
                requestParams = objectMapper.writeValueAsString(args[1]); // 파라미터
            }
            log.info("External API call to {} with params {}", apiUrl, requestParams != null ? objectMapper.writeValueAsString(requestParams) : "No parameters");
        } else {
            log.warn("No parameters passed to external API call.");
        }
        long startTime = System.currentTimeMillis(); // 시작 시간 기록
        Object result;
        try {
            result = joinPoint.proceed(); // 실제 메서드 호출
        } catch (Exception e) {
            log.error("Error during API call to {}: {}", apiUrl, e.getMessage());
            throw e; // 예외를 다시 던져서 호출자에게 전달
        }

        // 응답 및 시간 로깅
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("API response: {} - Time taken: {} ms",
                result != null ? objectMapper.writeValueAsString(result) : "No response",
                elapsedTime);

        return result;
    }
}