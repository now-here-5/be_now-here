package com.now_here5.now_here.domain.member.controller;

import com.now_here5.now_here.domain.member.dto.*;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.member.service.MemberService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "회원 추천", description = "현재 로그인한 회원의 이벤트 ID와 성별을 사용하여 회원을 추천합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "M004 - 회원 추천에 성공했습니다.", content = @Content(schema = @Schema(implementation = MemberRecommendResponse.class)))
    @ApiResponse(responseCode = "400", description = "M004 - 회원 추천에 실패했습니다.")

    @GetMapping("/recommend")
    public ResponseEntity<ResponseForm> recommendMembers() {

        List<MemberRecommendResponse> memberRecommendResponse = memberService.recommendMembers();

        return memberRecommendResponse != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MEMBER_RECOMMEND_SUCCESS, memberRecommendResponse)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MEMBER_RECOMMEND_FAIL));
    }

    @Operation(summary = "회원이 참여한 이벤트 조회", description = "회원이 참여한 이벤트 목록을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "E004 - 이벤트 목록 조회 성공", content = @Content(schema = @Schema(implementation = EventListResponse.class)))
    @ApiResponse(responseCode = "400", description = "E004 - 이벤트 목록 조회 실패")

    @GetMapping("/assigned-event")
    public ResponseEntity<ResponseForm> getAssignedEvents() {

        EventListResponse eventListResponse = memberService.getAssignedEventsByMember();

        return eventListResponse != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MY_EVENTS_QUERY_SUCCESS, eventListResponse)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MY_EVENTS_QUERY_FAIL));
    }
}