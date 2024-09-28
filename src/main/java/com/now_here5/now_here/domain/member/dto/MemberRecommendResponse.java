package com.now_here5.now_here.domain.member.dto;

import lombok.Getter;


@Getter
public class MemberRecommendResponse extends CommonMemberResponse {
    private final boolean isNotificationOn;

    public MemberRecommendResponse(Long memberId, String mbti, String nickname,
                                   String birthdate, String gender, String description, boolean isNotificationOn) {
        super(memberId, mbti, nickname, birthdate, gender, description);
        this.isNotificationOn = isNotificationOn;
    }
}