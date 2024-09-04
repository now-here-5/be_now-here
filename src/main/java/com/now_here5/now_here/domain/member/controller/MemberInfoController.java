package com.now_here5.now_here.domain.member.controller;


import com.now_here5.now_here.domain.member.dto.*;
import com.now_here5.now_here.domain.member.service.MemberService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member Setting API", description = "회원 설정 관련 API")
public class MemberInfoController {

    private final MemberService memberService;

    @Operation(summary = "개인정보 조회", description = "회원의 개인정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "M006 - 개인정보 조회에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "M006 - 개인정보 조회에 실패했습니다.")

    @GetMapping("/read/personal-info")
    public ResponseEntity<ResponseForm> getPersonalInfo() {

        PersonalInfoResponse infoResponse = memberService.getPersonalInfo();

        return infoResponse != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PERSONAL_INFO_QUERY_SUCCESS, infoResponse)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PERSONAL_INFO_QUERY_FAIL));
    }

    @Operation(summary = "프로필 조회", description = "회원의 프로필을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "M005 - 프로필 조회에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "M005 - 프로필 조회에 실패했습니다.")

    @GetMapping("/read/profile")
    public ResponseEntity<ResponseForm> getProfile() {

        ProfileResponse profile = memberService.getProfile();

        return profile != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PROFILE_QUERY_SUCCESS, profile)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PROFILE_QUERY_FAIL));
    }

    @Operation(summary = "회원 설명 업데이트", description = "회원의 설명을 업데이트합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "M007 - 설명 업데이트에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "M007 - 설명 업데이트에 실패했습니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원 설명 업데이트 요청",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = UpdateDescriptionRequest.class,
                            requiredProperties = {"description"}
                    ),
                    examples = @ExampleObject(
                            description = "UpdateDescriptionRequestExample",
                            name = "UpdateDescriptionRequestExample",
                            summary = "Example of UpdateDescriptionRequest",
                            value = "{\"description\": \"A new description\"}"
                    )
            )
    )

    @PatchMapping("/update/description")
    public ResponseEntity<ResponseForm> updateDescription(
            @RequestBody UpdateDescriptionRequest updateDescriptionRequest) {

        boolean updated = memberService.updateDescription(updateDescriptionRequest.getDescription());

        return updated ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.DESCRIPTION_UPDATE_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.DESCRIPTION_UPDATE_FAIL));
    }

    @Operation(summary = "알림 설정 업데이트", description = "회원의 알림 설정을 업데이트합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "M009 - 알림 설정 변경에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "M009 - 알림 설정 변경에 실패했습니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "알림 설정 업데이트 요청",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = UpdateNotificationRequest.class,
                            requiredProperties = {"notification"}
                    ),
                    examples = @ExampleObject(
                            description = "UpdateNotificationRequestExample",
                            name = "UpdateNotificationRequestExample",
                            summary = "Example of UpdateNotificationRequest",
                            value = "{\"notification\": true}"
                    )
            )
    )

    @PatchMapping("/update/notification-setting")
    public ResponseEntity<ResponseForm> updateNotification(
            @RequestBody UpdateNotificationRequest updateNotificationRequest) {

        boolean updated = memberService.updateNotificationSetting(updateNotificationRequest.isNotification());

        return updated ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.TOGGLE_NOTIFICATION_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.TOGGLE_NOTIFICATION_FAIL));
    }


    @Operation(summary = "알림 설정 조회", description = "회원의 알림 설정을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "M009 - 알림 설정 조회에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "M009 - 알림 설정 조회에 실패했습니다.")
    @GetMapping("/notification-setting")
    public ResponseEntity<ResponseForm> getNotificationSetting() {
        try {
            boolean notificationSetting = memberService.getNotificationSetting();
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.TOGGLE_NOTIFICATION_SUCCESS, notificationSetting));
        } catch (Exception e) {
            log.error("알림 설정 조회에 실패했습니다: {}", e.getMessage());
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.TOGGLE_NOTIFICATION_FAIL));
        }
    }

    @Operation(summary = "닉네임 업데이트", description = "회원의 닉네임을 업데이트합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "M008 - 닉네임 업데이트에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "M008 - 닉네임 업데이트에 실패했습니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "닉네임 업데이트 요청",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = UpdateNicknameRequest.class,
                            requiredProperties = {"nickname"}
                    ),
                    examples = @ExampleObject(
                            description = "UpdateNicknameRequestExample",
                            name = "UpdateNicknameRequestExample",
                            summary = "Example of UpdateNicknameRequest",
                            value = "{\"nickname\": \"new_nickname\"}"
                    )
            )
    )

    @PatchMapping("/update/nickname")
    public ResponseEntity<ResponseForm> updateNickName(
            @RequestBody UpdateNicknameRequest updateNicknameRequest) {

        boolean updated = memberService.updateNickName(updateNicknameRequest.getNickname());

        return updated ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.NICKNAME_UPDATE_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.NICKNAME_UPDATE_FAIL));
    }

    @Operation(summary = "MBTI 업데이트", description = "회원의 MBTI를 업데이트합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "M010 - MBTI 업데이트에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "M010 - MBTI 업데이트에 실패했습니다.")

    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "MBTI 업데이트 요청",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = UpdateMbtiRequest.class,
                            requiredProperties = {"mbti"}
                    ),
                    examples = @ExampleObject(
                            description = "UpdateMbtiRequestExample",
                            name = "UpdateMbtiRequestExample",
                            summary = "Example of UpdateMbtiRequest",
                            value = "{\"mbti\": \"ENTP\"}"
                    )
            )
    )

    @PatchMapping("/update/mbti")
    public ResponseEntity<ResponseForm> updateMbti(
            @RequestBody UpdateMbtiRequest updateMbtiRequest) {

        boolean updated = memberService.updateMbti(updateMbtiRequest.getMbti());

        return updated ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MBTI_UPDATE_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MBTI_UPDATE_FAIL));
    }
}
