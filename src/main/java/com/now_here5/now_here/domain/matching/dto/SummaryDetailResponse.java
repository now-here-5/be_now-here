package com.now_here5.now_here.domain.matching.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SummaryDetailResponse {
    private final String memberId;
    private final String mbti;
    private final String birthdate;
    private final String nickname;
    private String gender;
    private String description;
    private String snsId;

    @Builder
    public SummaryDetailResponse(String memberId, String mbti, String birthdate, String nickname, String gender, String description, String snsId) {
        this.memberId = memberId;
        this.mbti = mbti;
        this.birthdate = birthdate;
        this.nickname = nickname;
        this.gender = gender;
        this.description = description;
        this.snsId = snsId;
    }
}
