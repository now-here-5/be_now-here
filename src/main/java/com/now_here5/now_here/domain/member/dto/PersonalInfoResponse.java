package com.now_here5.now_here.domain.member.dto;

import lombok.Getter;

@Getter
public class PersonalInfoResponse extends ProfileResponse {
    private final String accountId;
    private final String description;
    private final String string;

    public PersonalInfoResponse(Long memberId, String mbti, String nickname, String birthdate, String gender, String accountId, String description, String string) {
        super(memberId, mbti, nickname, birthdate, gender, description);
        this.accountId = accountId;
        this.description = description;
        this.string = string;
    }
}
