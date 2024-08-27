package com.now_here5.now_here.domain.member.controller;



import com.now_here5.now_here.domain.member.dto.MemberRecommendResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.member.dto.RegisterMemberRequest;
import com.now_here5.now_here.domain.member.service.MemberService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.infra.phone.service.PhoneService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member API", description = "사용자 관련 API")
public class MemberController {

    private final MemberService memberService;
    private final PhoneService phoneService;

    @Operation(summary = "휴대폰 번호 인증 요청", description = "인증 전 같은 이벤트로 휴대폰이 중복되는지 확인합니다.")
    @Parameters({
            @Parameter(name = "event_id", description = "이벤트 ID", required = true, schema = @Schema(example = "1")),
            @Parameter(name = "phone", description = "휴대폰 번호", required = true, schema = @Schema(example = "01012345678"))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "A001 - 현재 이벤트로 이미 가입된 번호입니다."),
            @ApiResponse(responseCode = "200", description = "A001 - 휴대폰 인증을 요청했습니다."),
            @ApiResponse(responseCode = "400", description = "A001 - 휴대폰 인증에 실패했습니다.")
    })
    @GetMapping("/verify/{event_id}")
    public ResponseEntity<ResponseForm> verifyPhone(
            @PathVariable(name = "event_id") Long eventId,
            @RequestParam(name = "phone") String phone) {
        boolean duplicated = memberService.checkPhoneDuplicated(eventId, phone);

        if (duplicated) {
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_DUPLICATED));
        }

        boolean sent = memberService.sendCode(phone);

        return sent ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_VERIFY_REQUEST)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_VERIFY_FAIL));
    }

    @Operation(summary = "인증 코드 조회", description = "개발용으로 휴대폰 번호를 사용하여 인증 코드를 조회합니다.")
    @Parameters({
            @Parameter(name = "phone", description = "휴대폰 번호", required = true, schema = @Schema(example = "01012345678"))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A001-D - 개발용 인증 코드를 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "A001-D - 개발용 인증 코드를 조회하지 못했습니다.")
    })
    @GetMapping("/verify/code")
    public ResponseEntity<ResponseForm> verifyPhone(
            @RequestParam(name = "phone") String phone) {

        String savedCode = phoneService.getPhoneCode(phone);

        return savedCode != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_GET_SUCCESS, savedCode)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_GET_FAIL));
    }

    @Operation(summary = "닉네임 중복 확인", description = "이벤트 ID와 닉네임을 사용하여 닉네임 중복 여부를 확인합니다.")
    @Parameters({
            @Parameter(name = "event_id", description = "이벤트 ID", required = true, schema = @Schema(example = "1")),
            @Parameter(name = "nickname", description = "닉네임", required = true, schema = @Schema(example = "john_doe"))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A002 - 사용 가능한 닉네임입니다."),
            @ApiResponse(responseCode = "400", description = "A002 - 중복된 닉네임입니다.")
    })
    @GetMapping("/verify/nickname/{event_id}")
    public ResponseEntity<ResponseForm> checkIfNicknameIsDuplicated(
            @PathVariable(name = "event_id", required = true) Long eventId,
            @RequestParam(name = "nickname", required = true) String nickname) {


        boolean isDuplicated = memberService.checkNicknameDuplicated(eventId, nickname);

        return isDuplicated ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.NICKNAME_DUPLICATED)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.NICKNAME_QUALIFIED));
    }

    @Operation(summary = "인증 코드 확인", description = "휴대폰 번호와 인증 코드를 사용하여 인증 코드를 확인합니다.")
    @Parameters({
            @Parameter(name = "phone", description = "휴대폰 번호", required = true, schema = @Schema(example = "01012345678")),
            @Parameter(name = "code", description = "인증 코드", required = true, schema = @Schema(example = "123456"))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A001 - 휴대폰 인증에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "A001 - 휴대폰 인증에 실패했습니다.")
    })
    @PostMapping("/verify/code")

    public ResponseEntity<ResponseForm> verifyReceivedCode(
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "code") String code) {

        boolean isVerified = memberService.verifyCode(phone, code);
        return isVerified ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_VERIFY_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_VERIFY_FAIL));
    }

    @Operation(summary = "회원 등록", description = "이벤트 ID와 회원 등록 요청 정보를 사용하여 회원을 등록합니다.")
    @Parameters({
            @Parameter(name = "event_id", description = "이벤트 ID", required = true, schema = @Schema(example = "1")),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원 등록 요청",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = RegisterMemberRequest.class,
                            requiredProperties = {"phone", "password", "nickname", "birth", "mbti", "gender", "description"}
                    ),
                    examples = @ExampleObject(
                            description = "RegisterMemberRequestExample",
                            name = "RegisterMemberRequestExample",
                            summary = "Example of RegisterMemberRequest",
                            value = "{\"phone\": \"01012345678\", \"password\": \"password123\", \"nickname\": \"user123\", \"birth\": \"1990-01-01\", \"mbti\": \"INTJ\", \"gender\": \"male\", \"description\": \"A brief description\"}"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "S001 - 회원가입에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "S001 - 회원가입에 실패했습니다.")
    })

    @PostMapping("/register/{event_id}")
    public ResponseEntity<ResponseForm> registerMember(

            @PathVariable(name = "event_id", required = true) Long eventId,
            @RequestBody RegisterMemberRequest registerMemberRequest) {

        String token = memberService.registerMember(eventId, registerMemberRequest);


        return token != null?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SIGNUP_SUCCESS, token)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SIGNUP_FAIL));
    }


    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 시도합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M003 - 회원탈퇴에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "M003 - 회원탈퇴에 실패했습니다.")
    })
    @DeleteMapping("/inactivate")
    public ResponseEntity<ResponseForm> inactivateMember() {

        boolean inactivated = memberService.inactivateMember();
        return inactivated ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.INACTIVATE_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.INACTIVATE_FAIL));
    }




    @Operation(summary = "회원 추천", description = "현재 로그인한 회원의 이벤트 ID와 성별을 사용하여 회원을 추천합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M004 - 회원 추천에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "M004 - 회원 추천에 실패했습니다.")
    })
    @GetMapping("/recommend")
    public ResponseEntity<ResponseForm> recommendMembers() {

        List<MemberRecommendResponse> memberRecommendResponse = memberService.recommendMembers();

        return memberRecommendResponse != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MEMBER_RECOMMEND_SUCCESS, memberRecommendResponse)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MEMBER_RECOMMEND_FAIL));
    }


    @Operation(summary = "회원이 참여한 이벤트 조회", description = "회원이 참여한 이벤트 목록을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "E004 - 이벤트 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "E004 - 이벤트 목록 조회 실패")
    })

    @GetMapping("/assigned-event")
    public ResponseEntity<ResponseForm> getAssignedEvents() {

        EventListResponse eventListResponse = memberService.getAssignedEventsByMember();

        return eventListResponse != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MY_EVENTS_QUERY_SUCCESS, eventListResponse)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MY_EVENTS_QUERY_FAIL));
    }
}

