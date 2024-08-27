package com.now_here5.now_here.domain.interaction.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedbackRequect {
    private String content;
    private int field;
}

