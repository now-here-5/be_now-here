package com.now_here5.now_here.domain.member.service;

import com.now_here5.now_here.domain.member.dto.LoginRequest;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.domain.member.repository.MemberAuthRepository;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.global.security.converter.ListRolesToDto;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.security.dto.TokenDto;
import com.now_here5.now_here.global.security.provider.TokenGenerator;
import com.now_here5.now_here.global.security.service.CustomAuthenticationToken;
import com.now_here5.now_here.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor

public class MemberAuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberAuthRepository memberAuthRepository;
    private final MemberRepository memberRepository;
    private final TokenGenerator tokenGenerator;
    private final AuthUtil authUtil;
    private final ListRolesToDto listRolesToDto;

    @Transactional
    public boolean reactivateMember(Long memberId) {
        try {
            memberRepository.findMemberById(memberId).activate();
            return true;
        } catch (Exception e) {
            log.error("reactivate Member Error ={}", e.getMessage());
            return false;
        }
    }

    @Transactional
    public TokenDto login(LoginRequest loginRequest, Long eventId) {
        try {
            setAuthentication(loginRequest, eventId); // 인증 & 인가

            String newToken = tokenGenerator.generateUniqueToken();

            Authentication authentication = authUtil.getAuthentication();
            Member tempMember = (Member) authentication.getPrincipal();
            memberAuthRepository.updateTokenById(newToken, tempMember.getId());

            return new TokenDto(newToken);
        } catch (Exception e) {
            log.error("login Error ={}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public boolean logout() {

        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();

        try {
            memberAuthRepository.updateTokenById(null, authMember.getMemberId()); // n
            return true;
        } catch (Exception e) {
            log.error("delete Token For Member error ={}", e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public AuthenticatedMemberDto getMemberByToken(String token)  {
        try {
            Member member = memberAuthRepository.findMemberByToken(token);
            if (member == null) {
                log.error("get Member By Token Error, Member Not Found");
                throw new AuthenticationException("Member Not Found") {};
            }
            return AuthenticatedMemberDto.builder()
                    .memberId(member.getId())
                    .nickname(member.getNickname())
                    .eventId(member.getEvent().getId())
                    .eventName(member.getEvent().getField())
                    .location(member.getEvent().getLocation().getLocationName())
                    .startsAt(member.getEvent().getPeriodStart())
                    .endsAt(member.getEvent().getPeriodEnd())
                    .status(member.getEvent().isStatus())
                    .roleNamesDto(listRolesToDto.converter(member.getMemberRoleList()))
                    .build();

        } catch (Exception e) {
            log.error("get Member By Token Error = {}", e.getMessage());
            throw new AuthenticationException("Member Not Found") {};
        }

    }


    private void setAuthentication(LoginRequest loginRequest, Long eventId) {

        // CustomAuthenticationToken 생성, 여기에 eventID를 추가
        CustomAuthenticationToken authenticationToken =
                new CustomAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword(), eventId);

        // 인증 성공 후 SecurityContext에 Authentication 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 인증 처리
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }
}
