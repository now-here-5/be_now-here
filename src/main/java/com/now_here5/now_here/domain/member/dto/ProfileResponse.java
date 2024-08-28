package com.now_here5.now_here.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter

public class ProfileResponse extends MemberRecommendResponse {
    public ProfileResponse(Long memberId, String mbti, String nickname, String birthdate, String gender){
        super(memberId, mbti, nickname, birthdate, gender);
    }
}