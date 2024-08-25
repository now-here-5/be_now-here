package com.now_here5.now_here.global.security.filter;

import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.domain.member.service.MemberAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthFilter extends GenericFilterBean {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final MemberAuthService memberAuthService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        log.trace("doFilter for token");

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String customToken = resolveToken(httpServletRequest);

        String requestURI = httpServletRequest.getRequestURI();
        log.trace("resolveToken : {}, requestURI : {} ",customToken, requestURI);

        if (StringUtils.hasText(customToken) && memberAuthService.validateAuthToken(customToken)) { // 토큰이 있을 때만 검증
            AuthenticatedMemberDto memberDto = memberAuthService.getMemberByToken(customToken);

            List<GrantedAuthority> authorities =
                    memberDto.getRoleNamesDto() != null ?
                            memberDto.getRoleNamesDto().getRoleNames().stream()
                                    .map(SimpleGrantedAuthority::new) // 역할을 권한으로 변환
                                    .collect(Collectors.toList())// 스트림 결과를 리스트로 변환
                            : new ArrayList<>(); // null일 경우 빈 리스트 반환


            // 인증 성공 시 SecurityContextHolder 설정
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(memberDto, null, authorities));

            log.debug("Security Context에 '{}' 인증 정보를 저장.",  SecurityContextHolder.getContext().getAuthentication().getName());
        } else {
            log.warn("유효한 custom 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(request, response); // 다음 필터로 넘어가기 (토큰이 없는 경우는 로그인으로)
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
