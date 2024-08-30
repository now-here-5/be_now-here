package com.now_here5.now_here.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter

@RequiredArgsConstructor
public class MemberRecommendResponse {
    private final Long memberId;
    private final String mbti;
    private final String nickname;
    private final String birthdate;
    private String gender;
    private String description;

    @Builder
    public MemberRecommendResponse(Long memberId, String mbti, String nickname, String birthdate, String gender, String description) {
        this.memberId = memberId;
        this.mbti = mbti;
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.description = description;
    }
}
