package com.now_here5.now_here.domain.matching.dto;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class ReceiverResponse {
    private final Long senderId;
    private final String mbti;
    private final String birthdate;
    private final String nickname;
    private String gender;
    private String description;

    @Builder
    public ReceiverResponse(Long senderId, String mbti, String birthdate, String nickname, String gender, String description) {
        this.senderId = senderId;
        this.mbti = mbti;
        this.birthdate = birthdate;
        this.nickname = nickname;
        this.gender = gender;
        this.description = description;
    }
}
