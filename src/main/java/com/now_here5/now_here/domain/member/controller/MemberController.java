package com.now_here5.now_here.domain.member.controller;


import com.now_here5.now_here.domain.member.dto.RegisterMemberRequest;
import com.now_here5.now_here.domain.member.service.MemberService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /*
    * 휴대폰 번호 인증 요청
    * 인증전 같은 이벤트로 휴대폰이 중복되는지 확인.
     */
    @GetMapping("/verify/{event_id}")
    public ResponseEntity<ResponseForm> verifyPhone(
            @PathVariable(name = "event_id", required = true) Long eventId,
            @RequestParam(name = "phone", required = true) String phone){

        boolean duplicated = memberService.checkPhoneDuplicated(eventId, phone);

        if(duplicated){
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_DUPLICATED));
        }

        boolean sent = memberService.sendCode(phone);

        return sent ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_VERIFY_REQUEST)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_VERIFY_FAIL));
    }


    @GetMapping("/check-nickname/{event_id}/")
    public ResponseEntity<ResponseForm> checkIfNicknameIsDuplicated(
            @PathVariable(name = "event_id", required = true) Long eventId,
            @RequestParam(name = "nickname", required = true) String nickname){

        boolean duplicated = memberService.checkNicknameDuplicated(eventId, nickname);

        return duplicated ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.NICKNAME_DUPLICATED)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.NICKNAME_QUALIFIED));
    }

    @PostMapping("/verify/code")
    public ResponseEntity<ResponseForm> verifyReceivedCode(@RequestParam String phone, @RequestParam String code){
        boolean verified = memberService.verifyCode(phone, code);

        return verified ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_VERIFY_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.PHONE_VERIFY_FAIL));
    }

    @PostMapping("/register/{event_id}")
    public ResponseEntity<ResponseForm> registerMember(
            @PathVariable(name = "event_id", required = true) Long eventId,
            @RequestBody RegisterMemberRequest registerMemberRequest){

        String token = memberService.registerMember(eventId, registerMemberRequest);

        return token !=null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SIGNUP_SUCCESS,token)):
                ResponseEntity.ok(ResponseForm.of(ResponseCode.SIGNUP_FAIL));
    }

    @GetMapping("/inactivate")
    public ResponseEntity<ResponseForm> inactivateMember(){

        boolean inactivated = memberService.inactivateMember();
        return inactivated?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.INACTIVATE_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.INACTIVATE_FAIL));
    }
}
