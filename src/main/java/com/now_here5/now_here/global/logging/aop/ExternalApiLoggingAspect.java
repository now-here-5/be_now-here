package com.now_here5.now_here.global.logging.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExternalApiLoggingAspect {

    // @ExternalApiLogging 어노테이션이 적용된 메소드에만 AOP 적용
    @Around("@annotation(com.now_here5.now_here.global.logging.annotation.ExternalApiLogging)")
    public Object logExternalApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        // 시작 시간 기록
        long startTime = System.currentTimeMillis();

        // 메서드 이름과 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        String apiUrl = (String) args[0]; // API URL
        Object requestParams = args[1]; //  파라미터
        log.info("External API call to {} with params {}", apiUrl, requestParams);

        // 실제 메서드 호출
        Object result = joinPoint.proceed();

        // 응답 및 시간 로깅
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("API response: {} - Time taken: {} ms", result, elapsedTime); // 응답과 걸린 시간 기록

        return result;
    }
}
