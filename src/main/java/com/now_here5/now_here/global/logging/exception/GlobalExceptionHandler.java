package com.now_here5.now_here.global.logging.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUrl = request.getRequestURI();
        String httpMethod = request.getMethod();
        String clientIp = request.getRemoteAddr();
        LocalDateTime timestamp = LocalDateTime.now();

        // 상세한 예외 로그 기록
        log.error("Exception occurred at [{}] - Method: {}, URL: {}, Client IP: {}, Exception: {}",
                timestamp, httpMethod, requestUrl, clientIp, e.getMessage(), e);

        // 예외 응답 JSON 데이터
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", timestamp.toString());
        responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseBody.put("error", "Internal Server Error");
        responseBody.put("message", "서버 내부 오류가 발생했습니다. 문제가 지속되면 관리자에게 문의하세요.");
        responseBody.put("contact", "관리자 이메일: now.here.linker@gmail.com");
        responseBody.put("path", requestUrl);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseBody);
    }

    // 404 Not Found 에러 처리 (JSON 응답)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundJson(NoHandlerFoundException ex) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpStatus.NOT_FOUND.value());
        responseBody.put("error", "Not Found");
        responseBody.put("message", "페이지를 찾을 수 없습니다.");
        responseBody.put("path", request.getRequestURI());

        log.warn("NoHandlerFound : {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseBody);
    }

    // 400 Bad Request: 유효성 검사 실패 시 발생
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("error", "Bad Request");
        responseBody.put("message", "유효성 검사에 실패했습니다.");
        responseBody.put("details", ex.getBindingResult().getFieldErrors());

        log.warn("Validation failed: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBody);
    }

    // 400 Bad Request: 잘못된 인자 값
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("error", "Bad Request");
        responseBody.put("message", ex.getMessage());

        log.warn("Illegal argument provided: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBody);
    }

    // 500 Internal Server Error: NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseBody.put("error", "Internal Server Error");
        responseBody.put("message", "서버 내부 오류가 발생했습니다. 예상치 못한 null 값이 발생했습니다.");

        log.error("NullPointerException occurred: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseBody);
    }
}
