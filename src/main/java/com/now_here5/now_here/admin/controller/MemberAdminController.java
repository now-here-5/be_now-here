package com.now_here5.now_here.admin.controller;


import com.now_here5.now_here.domain.member.repository.MemberAuthRepository;
import com.now_here5.now_here.domain.member.service.MemberAuthService;
import com.now_here5.now_here.global.response.ResponseCode;
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

    @PatchMapping("/reactivate/{member_id}")
    ResponseEntity<ResponseCode> reactivateMember(@PathVariable(name = "member_id") Long memberId) {
        boolean activated = memberAuthService.reactivateMember(memberId);

        return activated ?
                ResponseEntity.ok(ResponseCode.MEMBER_REACTIVATE_SUCCESS) :
                ResponseEntity.ok(ResponseCode.MEMBER_REACTIVATE_FAIL);
    }

}
