package com.now_here5.now_here.global.security.dto;


import com.now_here5.now_here.domain.event.dto.EventResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

//@Schema(description = "인증된 멤버 DTO")
@Builder
@Getter
@RequiredArgsConstructor
public class AuthenticatedMemberDto {

    private final Long memberId;
    private final String nickname;
    private final Long eventId;
    private final String eventName;
    private final String location;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
    private final boolean status;
    private final RoleNamesDto roleNamesDto;
}
