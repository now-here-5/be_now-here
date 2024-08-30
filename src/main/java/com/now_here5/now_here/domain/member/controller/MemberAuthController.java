package com.now_here5.now_here.domain.member.controller;

import com.now_here5.now_here.domain.member.dto.LoginRequest;
import com.now_here5.now_here.domain.member.dto.LoginResponse;
import com.now_here5.now_here.domain.member.service.MemberAuthService;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.util.AuthUtil;
import com.now_here5.now_here.global.util.CustomXOR;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Member Auth API", description = "사용자 인증 & 인가 API")
public class MemberAuthController {

    private final MemberAuthService memberAuthService;
    private final CustomXOR customXOR;

    @Operation(summary = "로그인", description = "로그인을 시도하고, 반환값으로 토큰과 사용자가 참여중인 이벤트 목록을 반환합니다.")
    @Parameters({
            @Parameter(name = "event_id", description = "이벤트 ID", required = true, schema = @Schema(example = "MTAyOTM4NDY")),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "로그인 요청",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = LoginRequest.class,
                            requiredProperties = {"phone", "password"}
                    ),
                    examples = @ExampleObject(
                            name = "LoginRequestExample",
                            summary = "Example of LoginRequest",
                            value = "{\"phone\": \"01012345678\", \"password\": \"1234\"}"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M001 - 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "M001 - 로그인 실패")
    })
    @PostMapping("/login/{event_id}")
    public ResponseEntity<ResponseForm> login(
            @RequestBody LoginRequest loginRequest,
            @PathVariable(value = "event_id") String eventId
    ) {

        LoginResponse loginResponse = memberAuthService.login(loginRequest, customXOR.decrypt(eventId));
        return loginResponse != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOGIN_SUCCESS, loginResponse)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOGIN_FAIL));
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 시도합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M002 - 로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "M002 - 로그아웃 실패")
    })
    @DeleteMapping("/logout")
    public ResponseEntity<ResponseForm> logout() {
        boolean isLoggedOut = memberAuthService.logout();
        return isLoggedOut ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOGOUT_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOGOUT_FAIL));
    }
}