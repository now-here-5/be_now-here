package com.now_here5.now_here.admin.admin.controller;

import com.now_here5.now_here.domain.member.dto.LoginRequest;
import com.now_here5.now_here.domain.member.service.MemberAuthService;
import com.now_here5.now_here.global.security.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberAuthService memberAuthService;

    @GetMapping("/dev/login")
    public String loginPage() {
        return "dev/login"; // 로그인 페이지
    }

    @PostMapping("/dev/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // 로그인 시도
        TokenDto token = memberAuthService.login(LoginRequest.builder()
                .phoneNumber(loginRequest.getPhoneNumber())
                .password(loginRequest.getPassword())
                .build(), 1L);

        if (token != null) {
            // 로그인 성공 시 Swagger UI 페이지로 리다이렉트하며 토큰을 URL 매개변수로 전달
            return ResponseEntity.ok("나중에 계속..");
        }

        // 로그인 실패 시 오류 메시지 추가
        return ResponseEntity.badRequest().build();
    }
}
