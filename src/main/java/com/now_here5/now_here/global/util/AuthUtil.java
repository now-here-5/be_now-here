package com.now_here5.now_here.global.util;

import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUtil {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public AuthenticatedMemberDto getMemberByAuthentication() {
        return (AuthenticatedMemberDto) getAuthentication().getPrincipal();
    }
}
