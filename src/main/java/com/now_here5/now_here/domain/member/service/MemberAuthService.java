package com.now_here5.now_here.domain.member.service;


import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.domain.member.dto.LoginRequest;
import com.now_here5.now_here.global.security.dto.TokenDto;
import com.now_here5.now_here.global.security.provider.TokenGenerator;
import com.now_here5.now_here.domain.member.repository.MemberAuthRepository;
import com.now_here5.now_here.global.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor

public class MemberAuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberAuthRepository memberAuthRepository;
    private final TokenGenerator tokenGenerator;
    private final AuthUtil authUtil;

    @Transactional
    public TokenDto login(LoginRequest loginRequest){

        try {
            setAuthentication(loginRequest); // 인증 & 인가
            String newToken = tokenGenerator.generateUniqueToken();
            AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
            memberAuthRepository.updateTokenById(newToken, authMember.getUserId());
            return new TokenDto(newToken);
        } catch (Exception e) {
            log.error("save Token For Member error ={}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public boolean logout(){

        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();

        try {
            memberAuthRepository.updateTokenById(null, authMember.getUserId()); // n
            return true;
        } catch (Exception e) {
            log.error("delete Token For Member error ={}", e.getMessage());
            return false;
        }
    }

    public boolean validateAuthToken(String token) {

        try{
            AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
            return memberAuthRepository.isValidToken(token, authMember.getUserId());
        }catch(Exception e ) {
            log.error("validate Auth Token Error ={}", e.getMessage());
            return false;
        }
    }


    public AuthenticatedMemberDto getMemberByToken(String token) {
       return AuthenticatedMemberDto.builder()
               .userId(1L)
               .phoneNumber("010-1234-5678")
               .nickname("nickname")
               .build();
    }


    private void setAuthentication(LoginRequest loginRequest) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getPhone(), loginRequest.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

}
