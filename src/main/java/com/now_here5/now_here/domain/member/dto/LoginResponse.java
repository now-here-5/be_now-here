package com.now_here5.now_here.domain.member.dto;


import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.global.security.dto.TokenDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LoginResponse {
    private final TokenDto token;
    private final EventListResponse eventListResponse;
}
