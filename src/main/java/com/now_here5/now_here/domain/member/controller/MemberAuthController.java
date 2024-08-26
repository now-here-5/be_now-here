package com.now_here5.now_here.domain.member.controller;

import com.now_here5.now_here.domain.member.dto.LoginRequest;
import com.now_here5.now_here.domain.member.dto.LoginResponse;
import com.now_here5.now_here.global.security.dto.TokenDto;
import com.now_here5.now_here.domain.member.service.MemberAuthService;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.global.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Member Auth API", description = "사용자 인증 & 인가 API")
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    @Operation(summary = "로그인", description = "로그인을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M001 - 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "M002 - 로그인 실패")
    })
    @PostMapping("/login/{event_id}")
    public ResponseEntity<ResponseForm> login(
            @Parameter(description = "로그인 요청 정보", required = true) @RequestBody LoginRequest loginRequest,
            @Parameter(description = "이벤트 ID", required = true) @PathVariable(value = "event_id") Long eventId
    ) {
        LoginResponse loginResponse = memberAuthService.login(loginRequest, eventId);
        return loginResponse != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOGIN_SUCCESS, loginResponse)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOGIN_FAIL));
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 시도합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M003 - 로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "M004 - 로그아웃 실패")
    })
    @GetMapping("/logout")
    public ResponseEntity<ResponseForm> logout() {
        boolean isLoggedOut = memberAuthService.logout();
        return isLoggedOut ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOGOUT_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOGOUT_FAIL));
    }
}