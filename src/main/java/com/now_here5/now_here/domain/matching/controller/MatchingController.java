package com.now_here5.now_here.domain.matching.controller;

import com.now_here5.now_here.domain.matching.dto.*;
import com.now_here5.now_here.domain.matching.service.MatchingService;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.global.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/matching")
@Tag(name = "Matching API", description = "매칭 API")
public class MatchingController {
    private final MatchingService matchingService;

    @Operation(summary = "배너용 멤버 조회", description = "배너에 표시할 멤버 목록을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M001 - 배너용 멤버 목록 조회에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "M001 - 배너용 멤버 목록 조회에 실패했습니다.")
    })
    @GetMapping("/banner")
    public ResponseEntity<ResponseForm> getMemberForBanner() {
        List<BannerListResponse> bannerList = matchingService.getBannerList();

        return bannerList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.BannerList_QUERY_SUCCESS, bannerList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.BannerList_QUERY_FAIL));
    }

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<ResponseForm> sendLove(@PathVariable(name = "receiverId") Long receiverId) {
        try {
            matchingService.sendLove(receiverId);
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.LOVE_SEND_SUCCESS));
        } catch (Exception e) {
            log.error("하트 보내기 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.LOVE_SEND_FAIL));
        }
    }

    @PatchMapping("/receive/{senderId}")
    public ResponseEntity<ResponseForm> receiveLove(@PathVariable(name = "senderId") Long senderId) {
        try {
            matchingService.receiveLove(senderId);
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.LOVE_RECEIVE_SUCCESS));
        } catch (Exception e) {
            log.error("하트 받기 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.LOVE_RECEIVE_FAIL));
        }
    }
    @GetMapping("/summary")
    public ResponseEntity<ResponseForm> getSummary() {
        List<SummaryResponse> summary = matchingService.getSummary();

        return summary != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SUMMARY_GET_SUCCESS, summary)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SUMMARY_GET_FAIL));
    }

    @GetMapping("/senderList")
    public ResponseEntity<ResponseForm> getSenderList() {
        List<SenderResponse> senderList = matchingService.getSenderList();

        return senderList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SENDERLIST_QUERY_SUCCESS, senderList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SENDERLIST_QUERY_FAIL));
    }

    @GetMapping("/receiverList")
    public ResponseEntity<ResponseForm> getReceiverList() {
        List<ReceiverResponse> receiverList = matchingService.getReceiverList();

        return receiverList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.RECEIVERLIST_QUERY_SUCCESS, receiverList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.RECEIVERLIST_QUERY_FAIL));
    }
    @GetMapping("/summaryDetail")
    public ResponseEntity<ResponseForm> getSummaryDetail() {
        List<SummaryDetailResponse> summaryDetailResponseList = matchingService.getAcceptedMatchings();

        return summaryDetailResponseList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SUMMARYDETAIL_GET_SUCCESS, summaryDetailResponseList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SUMMARY_GET_FAIL));
    }
}