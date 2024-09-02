package com.now_here5.now_here.domain.interaction.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InquiryResponse {
    private final Long inquiryId;
    private final String answer;
    private final String inquiryContent;
}
