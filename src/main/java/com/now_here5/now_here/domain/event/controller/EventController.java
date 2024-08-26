package com.now_here5.now_here.domain.event.controller;

import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.dto.EventTimeResponse;
import com.now_here5.now_here.domain.event.service.EventService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    @Operation(summary = "이벤트 목록 조회", description = "이벤트의 상태에 따라 목록을 조회합니다.")
    @Parameters({
            @Parameter(name = "status", required = true, example = "true", description = "이벤트 상태")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "E003 - 이벤트 목록 조회에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "E003 - 이벤트 목록 조회에 실패했습니다.")
    })
    @GetMapping("/list")
    public ResponseEntity<ResponseForm> getEventList(
            @RequestParam(name = "status", required = true) boolean status){

        EventListResponse eventList = eventService.getEventList(status);

        return eventList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENTTLIST_QUERY_SUCCESS, eventList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENTTLIST_QUERY_FAIL));
    }

    @Operation(summary = "이벤트 상세 조회", description = "이벤트 ID를 통해 이벤트의 상세 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "event_id", required = true, example = "1", description = "이벤트 ID")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "E004 - 이벤트 조회에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "E004 - 이벤트 조회에 실패했습니다.")
    })
    @GetMapping("/detail/{event_id}")
    public ResponseEntity<ResponseForm> getEventList(
            @PathVariable(name = "event_id", required = true) Long eventId){

        EventResponse eventDetail = eventService.getEventDetail(eventId);

        return eventDetail != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_SUCCESS, eventDetail)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_FAIL));
    }

    @Operation(summary = "이벤트 남은 시간 조회", description = "현재 시간과 이벤트 종료 시간의 차이를 계산하여 남은 시간을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "E005 - 이벤트 시간 조회에 성공했습니다.", content = @Content(schema = @Schema(implementation = EventTimeResponse.class))),
            @ApiResponse(responseCode = "400", description = "E005 - 이벤트 시간 조회에 실패했습니다.")
    })
    @GetMapping("/time")
    public ResponseEntity<ResponseForm> getEventTime(){

        EventTimeResponse eventTime = eventService.getEventTime();

        return eventTime != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_TIME_SUCCESS, eventTime)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_TIME_FAIL));
    }
}