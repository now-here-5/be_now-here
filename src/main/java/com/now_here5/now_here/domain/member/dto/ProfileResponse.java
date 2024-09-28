package com.now_here5.now_here.domain.member.dto;

import lombok.Getter;


@Getter
public class ProfileResponse extends CommonMemberResponse {
    public ProfileResponse(Long memberId, String mbti, String nickname, String birthdate, String gender, String description) {
        super(memberId, mbti, nickname, birthdate, gender, description);
    }
}