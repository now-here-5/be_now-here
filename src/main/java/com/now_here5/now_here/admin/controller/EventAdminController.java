package com.now_here5.now_here.admin.controller;

import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.domain.event.dto.NewEventRequest;
import com.now_here5.now_here.domain.event.service.EventSchedulerService;
import com.now_here5.now_here.domain.event.service.EventService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.global.util.CustomXOR;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/admin/event")
@Tag(name = "Admin Event management API", description = "관리자 이벤트 관리 API")
public class EventAdminController {
    private final EventService eventService;
    private final EventSchedulerService eventSchedulerService;
    private final CustomXOR customXOR;

    @Operation(summary = "이벤트 목록 조회", description = "상태에 따라 이벤트 목록을 조회합니다.")
    @Parameter(name = "status", description = "이벤트 상태", required = true, schema = @Schema(example = "true"))
    @ApiResponse(responseCode = "200", description = "E001 - 이벤트 목록 조회 성공")
    @ApiResponse(responseCode = "400", description = "E001 - 이벤트 목록 조회 실패")

    @GetMapping("/list")
    public ResponseEntity<ResponseForm> getEventList(
            @RequestParam(name = "status", required = true) boolean status) {

        EventListResponse eventList = eventService.getEventList(status);

        return eventList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENTLIST_QUERY_SUCCESS, eventList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENTLIST_QUERY_FAIL));
    }

    @Operation(summary = "이벤트 상세 조회", description = "이벤트 ID를 사용하여 이벤트의 세부 정보를 조회합니다.")
    @Parameter(name = "event_id", description = "이벤트 ID", required = true, schema = @Schema(example = "MTAyOTM5"))
    @ApiResponse(responseCode = "200", description = "E005 - 이벤트 상세 조회 성공")
    @ApiResponse(responseCode = "400", description = "E006 - 이벤트 상세 조회 실패")

    @GetMapping("/detail/{event_id}")
    public ResponseEntity<ResponseForm> getSingleEvent(
            @PathVariable(name = "event_id", required = true) String eventId) {

        EventResponse eventDetail = eventService.getEventDetail(customXOR.decrypt(eventId));

        return eventDetail != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_SUCCESS, eventDetail)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_FAIL));
    }

    @Operation(summary = "이벤트 삭제", description = "이벤트 ID를 사용하여 이벤트를 삭제합니다.")
    @Parameter(name = "event_id", description = "이벤트 ID", required = true, schema = @Schema(example = "MTAyOTM5"))
    @ApiResponse(responseCode = "200", description = "E009 - 이벤트 삭제 성공")
    @ApiResponse(responseCode = "400", description = "E010 - 이벤트 삭제 실패")

    @PatchMapping("/update/{event_id}")
    public ResponseEntity<ResponseForm> deleteEvent(
            @PathVariable(name = "event_id", required = true) String eventId,
            @RequestBody NewEventRequest request) {
        boolean result = eventSchedulerService.updateEvent(request, customXOR.decrypt(eventId));

        return result ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_DELETE_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_DELETE_FAIL));
    }

    @Operation(summary = "이벤트 종료", description = "이벤트 ID를 사용하여 이벤트를 종료합니다.")
    @Parameter(name = "event_id", description = "이벤트 ID", required = true, schema = @Schema(example = "MTAyOTM5"))
    @ApiResponse(responseCode = "200", description = "E007 - 이벤트 종료 성공")
    @ApiResponse(responseCode = "400", description = "E008 - 이벤트 종료 실패")

    @DeleteMapping("/close/{event_id}")
    public ResponseEntity<ResponseForm> closeEventWithMembers(
            @PathVariable(name = "event_id", required = true) String eventId) {

        try {
            eventSchedulerService.closeEventWithMembers(customXOR.decrypt(eventId));
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_CLOSE_SUCCESS));
        } catch (Exception e) {
            log.error("Failed to close event: {}", e.getMessage());
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_CLOSE_FAIL));
        }

    }

    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "새로운 이벤트 요청",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = NewEventRequest.class),
                    examples = @ExampleObject(
                            name = "NewEventRequest",
                            value = "{ \"startsAt\": \"2024-09-01T10:00:00\", \"endsAt\": \"2024-09-01T18:00:00\", \"status\": true, \"field\": \"Field A\", \"locationId\": 1 }"
                    )
            ))
    @ApiResponse(responseCode = "200", description = "E009 - 이벤트 생성 성공")
    @ApiResponse(responseCode = "400", description = "E010 - 이벤트 생성 실패")

    @PostMapping("/open")
    public ResponseEntity<ResponseForm> openEvent(
            @RequestBody NewEventRequest request) {
        boolean result = eventSchedulerService.openEvent(request);

        return result ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_OPEN_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_OPEN_FAIL));
    }


    @GetMapping("/scheduled-list")
    public ResponseEntity<ResponseForm> getScheduledEventList() {
        List<Long> eventSchedulerKeys = eventSchedulerService.getEventSchedulerKeys();

        return eventSchedulerKeys != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENTSCHEDULER_QUERY_SUCCESS, eventSchedulerKeys)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENTSCHEDULER_QUERY_FAIL));

    }
}
