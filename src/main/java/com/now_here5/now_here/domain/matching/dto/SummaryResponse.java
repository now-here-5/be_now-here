package com.now_here5.now_here.domain.matching.dto;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class SummaryResponse {
    private final String receiveLove;
    private final String sendLove;
}
