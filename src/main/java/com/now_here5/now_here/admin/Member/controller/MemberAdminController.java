package com.now_here5.now_here.admin.Member.controller;


import com.now_here5.now_here.domain.member.service.MemberAuthService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Admin Member management API", description = "관리자 멤버 관리 API")
@RequestMapping("/admin/member")
public class MemberAdminController {
    private final MemberAuthService memberAuthService;
    @Operation(summary = "탈퇴한 회원을 재활성화", description = "탈퇴한 회원을 재활성화합니다.")
    @Parameter(name = "member_id", description = "회원 ID", required = true, schema = @Schema(example = "1"))
    @ApiResponse(responseCode = "200", description = "E005 - 회원 재활성화에 성공했습니다.")
    @ApiResponse(responseCode = "400", description = "E005 - 회원 재활성화에 실패했습니다.")
    
    @PatchMapping("/reactivate/{member_id}")
    ResponseEntity<ResponseForm> reactivateMember(@PathVariable(name = "member_id") Long memberId) {
        boolean activated = memberAuthService.reactivateMember(memberId);

        return activated ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MEMBER_REACTIVATE_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.MEMBER_REACTIVATE_FAIL)  );
    }

}
