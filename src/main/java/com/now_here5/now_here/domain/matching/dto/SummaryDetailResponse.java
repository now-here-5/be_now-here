package com.now_here5.now_here.domain.matching.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SummaryDetailResponse {
    private String memberId;
    private String mbti;
    private String birthdate;
    private String nickname;
    private String gender;
    private String description;
    private String phoneNumber;
}
