package com.now_here5.now_here.domain.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class HeartRequest {
    private Long receiverId;

    @JsonProperty("isSpecialUsed")
    private boolean isSpecialUsed;
}
