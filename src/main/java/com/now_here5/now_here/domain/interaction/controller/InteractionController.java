package com.now_here5.now_here.domain.interaction.controller;

import com.now_here5.now_here.domain.interaction.dto.FeedbackRequect;
import com.now_here5.now_here.domain.interaction.dto.InquiryRequest;
import com.now_here5.now_here.domain.interaction.service.InteractionService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interaction")
@RequiredArgsConstructor
@Tag(name = "Interaction API", description = "의견 관련 API")
@Slf4j
public class InteractionController {

    private final InteractionService interactionService;

    @Operation(summary = "피드백 작성", description = "사용자가 피드백을 작성합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "F001 - 피드백 작성에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "F001 - 피드백 작성에 실패했습니다.")

    @PostMapping("/feedback")
    public ResponseEntity<ResponseForm> createFeedback(@RequestBody FeedbackRequect feedbackRequect) {
        try {
            interactionService.createFeedback(feedbackRequect);
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.FEEDBACK_CREATE_SUCCESS));
        } catch (Exception e) {
            log.error("피드백 작성 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.FEEDBACK_CREATE_FAIL));
        }
    }

    @Operation(summary = "문의 작성", description = "사용자가 문의를 작성합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "I001 - 문의 작성에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "I001 - 문의 작성에 실패했습니다.")

    @PostMapping("/inquiry")
    public ResponseEntity<ResponseForm> createInquiry(@RequestBody InquiryRequest inquiryRequest) {
        try {
            interactionService.createInquiry(inquiryRequest);
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.INQUIRY_CREATE_SUCCESS));
        } catch (Exception e) {
            log.error("문의 작성 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.INQUIRY_CREATE_FAIL));
        }
    }

    @Operation(summary = "팝업 상태 조회", description = "사용자에게 팝업을 뜨워야할지 판단합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "I001 - 문의 작성에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "I001 - 문의 작성에 실패했습니다.")
    @GetMapping("/feedback/status")
    public ResponseEntity<ResponseForm> getFeedbackStatus() {

        try{
            boolean status = interactionService.getFeedbackStatus();
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.FEEDBACK_STATUS_SUCCESS, status));
        }catch(Exception e){
            log.error("피드백 팝업 상태 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.FEEDBACK_STATUS_FAIL));

        }
    }

//    // DTO 형태로 변경
//    @Operation(summary = "사용자의 피드백 조회", description = "사용자가 작성한 피드백을 조회합니다.")
//    @GetMapping("/feedback/{memberId}")
//    public ResponseEntity<ResponseForm> getFeedbacksByMemberId(@PathVariable Long memberId) {
//        try {
//            List<Feedback> feedbacks = interactionService.getFeedbacksByMemberId(memberId);
//            return ResponseEntity.ok(ResponseForm.of(ResponseCode.FEEDBACK_QUERY_SUCCESS, feedbacks));
//        } catch (Exception e) {
//            log.error("피드백 조회 중 오류 발생: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.FEEDBACK_QUERY_FAIL));
//        }
//    }
//
//    @Operation(summary = "사용자의 문의 조회", description = "사용자가 작성한 문의를 조회합니다.")
//    @GetMapping("/inquiry/{memberId}")
//    public ResponseEntity<ResponseForm> getInquiriesByMemberId(@PathVariable Long memberId) {
//        try {
//            List<Inquiry> inquiries = interactionService.getInquiriesByMemberId(memberId);
//            return ResponseEntity.ok(ResponseForm.of(ResponseCode.INQUIRY_QUERY_SUCCESS, inquiries));
//        } catch (Exception e) {
//            log.error("문의 조회 중 오류 발생: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.INQUIRY_QUERY_FAIL));
//        }
//    }
//
//    @Operation(summary = "사용자의 탈퇴 사유 조회", description = "사용자가 작성한 탈퇴 사유를 조회합니다.")
//    @GetMapping("/withdrawal-reason/{memberId}")
//    public ResponseEntity<ResponseForm> getWithdrawalReasonsByMemberId(@PathVariable Long memberId) {
//        try {
//            List<WithdrawalReason> withdrawalReasons = interactionService.getWithdrawalReasonsByMemberId(memberId);
//            return ResponseEntity.ok(ResponseForm.of(ResponseCode.WITHDRAWAL_REASON_QUERY_SUCCESS, withdrawalReasons));
//        } catch (Exception e) {
//            log.error("탈퇴 사유 조회 중 오류 발생: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.WITHDRAWAL_REASON_QUERY_FAIL));
//        }
//    }
}
