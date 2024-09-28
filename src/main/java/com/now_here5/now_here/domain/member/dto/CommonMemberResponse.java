package com.now_here5.now_here.domain.member.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Getter
public class CommonMemberResponse {
    private final Long memberId;
    private final String mbti;
    private final String nickname;
    private final String birthdate;
    private final String gender;
    private final String description;
}
