package com.now_here5.now_here.domain.matching.controller;

import com.now_here5.now_here.domain.matching.dto.*;
import com.now_here5.now_here.domain.matching.service.MatchingService;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.global.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/matching")
@Tag(name = "Matching API", description = "매칭 API")
public class MatchingController {
    private final MatchingService matchingService;

    @Operation(summary = "배너 매칭 목록 조회", description = "배너에 표시할 멤버 목록을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "B001 - 배너 목록 조회 성공")
    @ApiResponse(responseCode = "400", description = "B001 - 배너 목록 조회 실패")

    @GetMapping("/banner")
    public ResponseEntity<ResponseForm> getMemberForBanner() {
        List<BannerListResponse> bannerList = matchingService.getBannerList();

        if (bannerList == null) {
            bannerList = List.of();
        }

        return ResponseEntity.ok(ResponseForm.of(ResponseCode.BANNER_LIST_QUERY_SUCCESS, bannerList));
    }

    @Operation(summary = "하트 보내기", description = "특정 사용자에게 하트를 보냅니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @Parameter(name = "receiverId", description = "받는 사람의 ID", required = true, schema = @Schema(example = "1"))
    @ApiResponse(responseCode = "200", description = "L001 - 하트 보내기 성공")
    @ApiResponse(responseCode = "400", description = "L001 - 하트 보내기 실패")

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<ResponseForm> sendLove(@PathVariable(name = "receiverId") Long receiverId) {
        try {
            matchingService.sendLove(receiverId);
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.LOVE_SEND_SUCCESS));
        } catch (Exception e) {
            log.error("하트 보내기 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.LOVE_SEND_FAIL));
        }
    }

    @Operation(summary = "하트 수락하기", description = "특정 사용자로부터 하트를 받습니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @Parameter(name = "senderId", description = "보내는 사람의 ID", required = true, schema = @Schema(example = "1"))
    @ApiResponse(responseCode = "200", description = "L002 - 하트 받기 성공")
    @ApiResponse(responseCode = "400", description = "L002 - 하트 받기 실패")

    @PatchMapping("/receive/{senderId}")
    public ResponseEntity<ResponseForm> receiveLove(@PathVariable(name = "senderId") Long senderId) {
        try {
            matchingService.receiveLove(senderId);
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.LOVE_RECEIVE_SUCCESS));
        } catch (Exception e) {
            log.error("하트 받기 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.LOVE_RECEIVE_FAIL));
        }
    }

    @Operation(summary = "하트 거절하기", description = "특정 사용자의 하트를 거절합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @Parameter(name = "senderId", description = "보내는 사람의 ID", required = true, schema = @Schema(example = "1"))
    @ApiResponse(responseCode = "200", description = "L003 - 하트 거절 성공")
    @ApiResponse(responseCode = "400", description = "L003 - 하트 거절 실패")

    @PatchMapping("/reject/{senderId}")
    public ResponseEntity<ResponseForm> rejecteLove(@PathVariable(name = "senderId") Long senderId) {
        try {
            matchingService.rejectLove(senderId);
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.LOVE_REJECT_SUCCESS));
        } catch (Exception e) {
            log.error("하트 거절 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.LOVE_REJECT_FAIL));
        }
    }

    @Operation(summary = "매칭 요약 조회", description = "매칭 요약 정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "S001 - 요약 정보 조회 성공")
    @ApiResponse(responseCode = "400", description = "S001 - 요약 정보 조회 실패")

    @GetMapping("/summary")
    public ResponseEntity<ResponseForm> getSummary() {
        List<SummaryResponse> summary = matchingService.getSummary();

        return summary != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SUMMARY_GET_SUCCESS, summary)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SUMMARY_GET_FAIL));
    }

    @Operation(summary = "받은 하트 페이지 조회", description = "하트를 보낸 사람 목록을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "S002 - 보낸 사람 목록 조회 성공")
    @ApiResponse(responseCode = "400", description = "S002 - 보낸 사람 목록 조회 실패")

    @GetMapping("/senderList")
    public ResponseEntity<ResponseForm> getSenderList() {
        List<SenderResponse> senderList = matchingService.getSenderList();

        return senderList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SENDER_LIST_QUERY_SUCCESS, senderList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SENDER_LIST_QUERY_FAIL));
    }

    @Operation(summary = "보낸 하트 페이지 조회", description = "하트를 받은 사람 목록을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "R001 - 받은 사람 목록 조회 성공")
    @ApiResponse(responseCode = "400", description = "R001 - 받은 사람 목록 조회 실패")

    @GetMapping("/receiverList")
    public ResponseEntity<ResponseForm> getReceiverList() {
        List<ReceiverResponse> receiverList = matchingService.getReceiverList();

        return receiverList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.RECEIVER_LIST_QUERY_SUCCESS, receiverList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.RECEIVER_LIST_QUERY_FAIL));
    }

    @Operation(summary = "매칭 현황 페이지 조회", description = "매칭 요약 상세 정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "S003 - 요약 상세 정보 조회 성공")
    @ApiResponse(responseCode = "400", description = "S003 - 요약 상세 정보 조회 실패")

    @GetMapping("/summaryDetail")
    public ResponseEntity<ResponseForm> getSummaryDetail() {
        List<SummaryDetailResponse> summaryDetailResponseList = matchingService.getAcceptedMatchings();

        return summaryDetailResponseList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SUMMARY_DETAIL_GET_SUCCESS, summaryDetailResponseList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SUMMARY_GET_FAIL));
    }

    @Operation(summary = "알림 목록 조회", description = "알림 목록을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "N001 - 알림 목록 조회 성공")
    @ApiResponse(responseCode = "400", description = "N001 - 알림 목록 조회 실패")

    @GetMapping("/getNotificationList")
    public ResponseEntity<ResponseForm> getNotificationList() {
        List<NotificationResponse> notificationResponseList = matchingService.getNotificationList();

        return notificationResponseList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.NOTIFICATION_LIST_QUERY_SUCCESS, notificationResponseList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.NOTIFICATION_LIST_QUERY_FAIL));
    }

    @Operation(summary = "알림 개수 조회", description = "읽지 않은 알림의 개수를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "N002 - 알림 개수 조회 성공")
    @ApiResponse(responseCode = "400", description = "N002 - 알림 개수 조회 실패")

    @GetMapping("/getNotification")
    public ResponseEntity<ResponseForm> getNotificationCount() {
        try {
            Integer notificationCount = matchingService.getNotificationCount();
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.NOTIFICATION_COUNT_QUERY_SUCCESS, notificationCount));
        } catch (Exception e) {
            log.error("알림 개수 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.NOTIFICATION_COUNT_QUERY_FAIL, 0));
        }
    }
}