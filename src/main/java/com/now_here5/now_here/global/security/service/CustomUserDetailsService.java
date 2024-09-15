package com.now_here5.now_here.global.security.service;

import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.global.security.converter.ListRolesToDto;
import com.now_here5.now_here.global.security.dto.RoleNamesDto;
import com.now_here5.now_here.domain.member.repository.MemberAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberAuthRepository memberAuthRepository;
    private final ListRolesToDto listRolesToDto;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // 주어진 사용자 정보를 전화번호를 기반으로 데이터베이스에서 찾아 UserDetails 객체로 반환
    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {

        // 이벤트 ID가 필요하다면, 커스텀 토큰에서 꺼내 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof CustomAuthenticationToken customToken) {

            Long eventId = customToken.getEventId();
            Member member = memberAuthRepository.findMemberWithRolesByPhoneNumber(phoneNumber, eventId);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(member, null, new ArrayList<>()
                    ));

            if(member == null) {
                log.warn("해당 phone_number : {} 로 이벤트 : {} 가입된 유저가 없습니다 ",eventId, phoneNumber);
                throw new UsernameNotFoundException("no user not found");
            }

            // UserDetails 객체로 변환하여 반환
            return new User(
                    member.getPhoneNumber(),
                    member.getPassword(),
                    getAuthorities(
                            listRolesToDto.converter(
                                    member.getMemberRoleList())
                    ));
        } else {
            log.warn("CustomAuthenticationToken이 아닌 다른 Authentication 타입이 감지되었습니다.");
            throw new UsernameNotFoundException("CustomAuthenticationToken");
        }

    }

    // 유저 권한을 설정하는 메서드
    private Collection<? extends GrantedAuthority> getAuthorities(RoleNamesDto roleNamesDto) {
        return roleNamesDto.getRoleNames().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}