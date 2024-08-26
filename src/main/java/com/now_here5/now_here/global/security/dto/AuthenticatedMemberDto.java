package com.now_here5.now_here.global.security.dto;


import com.now_here5.now_here.domain.event.dto.EventResponse;
import lombok.Builder;
import lombok.Getter;

//@Schema(description = "인증된 멤버 DTO")
@Builder
@Getter
public class AuthenticatedMemberDto {

    private final Long memberId;

    private final String nickname;

    private final EventResponse event;

    private final RoleNamesDto roleNamesDto;
}
