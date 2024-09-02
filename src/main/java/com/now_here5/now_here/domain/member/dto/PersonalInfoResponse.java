package com.now_here5.now_here.domain.member.dto;

import lombok.Getter;

@Getter
public class PersonalInfoResponse extends ProfileResponse {
    private final String phone;
    private final String description;

    public PersonalInfoResponse(Long memberId, String mbti, String nickname, String birthdate, String gender, String phone, String description) {
        super(memberId, mbti, nickname, birthdate, gender, description);
        this.phone = phone;
        this.description = description;
    }
}
