package com.now_here5.now_here.infra.notification.controller;

import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.infra.notification.service.FCMNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "알림 API")
@RequestMapping("/notification")
public class NotificationController {

    private final FCMNotificationService fcmNotificationService;

    @Operation(summary = "FCM 토큰 저장", description = "회원의 FCM 토큰을 저장합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "F001 - FCM 토큰 저장 성공")
    @ApiResponse(responseCode = "400", description = "F001 - FCM 토큰 저장 실패")
    @PostMapping("/saveFCMToken")
    public ResponseEntity<ResponseForm> saveFCMToken(
            @RequestParam String token,
            @RequestParam String memberId) {
        boolean saved = fcmNotificationService.saveFCMToken(token, memberId);

        return saved ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.FCM_TOKEN_SAVE_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.FCM_TOKEN_SAVE_FAIL));
    }
}