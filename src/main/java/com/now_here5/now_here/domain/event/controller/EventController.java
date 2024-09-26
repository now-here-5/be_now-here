package com.now_here5.now_here.domain.event.controller;

import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.domain.event.dto.EventTimeResponse;
import com.now_here5.now_here.domain.event.service.EventService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Event API", description = "이벤트 API")
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;
    private final AuthUtil authUtil;

    @Operation(summary = "이벤트 남은 시간 조회", description = "현재 시간과 이벤트 종료 시간의 차이를 계산하여 남은 시간을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "E003 - 이벤트 시간 조회에 성공했습니다.", content = @Content(schema = @Schema(implementation = EventTimeResponse.class)))
    @ApiResponse(responseCode = "400", description = "E003 - 이벤트 시간 조회에 실패했습니다.")

    @GetMapping("/time")
    public ResponseEntity<ResponseForm> getEventTime() {

        EventTimeResponse eventTime = eventService.getEventTime();

        return eventTime != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_TIME_SUCCESS, eventTime)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_TIME_FAIL));
    }

    @Operation(summary = "이벤트 토큰으로 상세 조회", description = "이벤트 토큰을 사용하여 이벤트의 세부 정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "E005 - 이벤트 상세 조회 성공")
    @ApiResponse(responseCode = "400", description = "E006 - 이벤트 상세 조회 실패")
    @GetMapping("/token/detail")
    public ResponseEntity<ResponseForm> getEventDetailByToken() {

        AuthenticatedMemberDto authenticatedMemberDto = authUtil.getMemberByAuthentication();
        EventResponse eventDetail = eventService.getEventDetail(authenticatedMemberDto.getEventId());

        return eventDetail != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_BY_TOKEN_SUCCESS, eventDetail)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_BY_TOKEN_FAIL));
    }

    @Operation(summary = "이벤트 목록 조회", description = "상태에 따라 이벤트 목록을 조회합니다.")
    @Parameter(name = "status", description = "이벤트 상태", required = true, schema = @Schema(example = "true"))
    @ApiResponse(responseCode = "200", description = "E001 - 이벤트 목록 조회 성공")
    @ApiResponse(responseCode = "400", description = "E001 - 이벤트 목록 조회 실패")

    @GetMapping("/list")
    public ResponseEntity<ResponseForm> getEventList(
            @RequestParam(name = "status") boolean status) {

        EventListResponse eventList = eventService.getEventList(status, false);

        return eventList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENTLIST_QUERY_SUCCESS, eventList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENTLIST_QUERY_FAIL));
    }

}