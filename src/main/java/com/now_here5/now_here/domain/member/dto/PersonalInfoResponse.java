package com.now_here5.now_here.domain.member.dto;

import lombok.Getter;

@Getter
public class PersonalInfoResponse extends ProfileResponse {
    private final String accountId;
    private final String snsId;

    public PersonalInfoResponse(Long memberId, String accountId,  String snsId,
                                String mbti, String nickname, String gender,
                                String birthdate, String description) {
        super(memberId, mbti, nickname, birthdate, gender, description);
        this.accountId = accountId;
        this.snsId = snsId;
    }
}
