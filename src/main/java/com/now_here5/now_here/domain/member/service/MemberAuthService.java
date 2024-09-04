package com.now_here5.now_here.domain.member.service;


import com.now_here5.now_here.domain.event.converter.EventListToDto;
import com.now_here5.now_here.domain.event.repository.EventRepository;
import com.now_here5.now_here.domain.member.dto.LoginResponse;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.global.security.converter.ListRolesToDto;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.domain.member.dto.LoginRequest;
import com.now_here5.now_here.global.security.dto.TokenDto;
import com.now_here5.now_here.global.security.provider.TokenGenerator;
import com.now_here5.now_here.domain.member.repository.MemberAuthRepository;
import com.now_here5.now_here.global.security.service.CustomAuthenticationToken;
import com.now_here5.now_here.global.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor

public class MemberAuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberAuthRepository memberAuthRepository;
    private final TokenGenerator tokenGenerator;
    private final AuthUtil authUtil;
    private final ListRolesToDto listRolesToDto;
    private final EventRepository eventRepository;
    private final EventListToDto eventListToDto;


    public LoginResponse login(LoginRequest loginRequest, Long eventId) {

        try {
            setAuthentication(loginRequest, eventId); // 인증 & 인가

            String newToken = tokenGenerator.generateUniqueToken();
            Authentication authentication = authUtil.getAuthentication();
            Member tempMember = (Member) authentication.getPrincipal();

            memberAuthRepository.updateTokenById(newToken, tempMember.getId());

            return LoginResponse.builder()
                    .token(new TokenDto(newToken))
                    .build();

        } catch (Exception e) {
            log.error("login Error ={}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public boolean logout(){

        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();

        try {
            memberAuthRepository.updateTokenById(null, authMember.getMemberId()); // n
            return true;
        } catch (Exception e) {
            log.error("delete Token For Member error ={}", e.getMessage());
            return false;
        }
    }


    public AuthenticatedMemberDto getMemberByToken(String token) {
        try{
            Member member = memberAuthRepository.findMemberByToken(token);
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
            
        }catch(Exception e) {
            log.error("get Member By Token Error ={}", e.getMessage());
            return null;
        }

    }


    private void setAuthentication(LoginRequest loginRequest, Long eventId) {

        /// CustomAuthenticationToken 생성, 여기에 eventID를 추가
        CustomAuthenticationToken authenticationToken =
                new CustomAuthenticationToken(loginRequest.getPhone(), loginRequest.getPassword(), eventId);

        // 인증 성공 후 SecurityContext에 Authentication 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 인증 처리
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }
}
