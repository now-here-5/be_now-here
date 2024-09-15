package com.now_here5.now_here.domain.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SenderResponse {
    private Long id;
    private String mbti;
    private String birthday;
    private String nickname;
    private String gender;
    private String description;
}
