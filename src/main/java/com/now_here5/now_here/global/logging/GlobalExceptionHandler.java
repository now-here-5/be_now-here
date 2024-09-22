package com.now_here5.now_here.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUrl = request.getRequestURI();
        String httpMethod = request.getMethod();
        String clientIp = request.getRemoteAddr();
        LocalDateTime timestamp = LocalDateTime.now();

        // 상세한 예외 로그 기록
        log.error("Exception occurred at [{}] - Method: {}, URL: {}, Client IP: {}, Exception: {}",
                timestamp, httpMethod, requestUrl, clientIp, e.getMessage(), e);

        // 예외 응답
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부 오류가 발생했습니다. 문제가 지속되면 관리자에게 문의하세요.");
    }
}
