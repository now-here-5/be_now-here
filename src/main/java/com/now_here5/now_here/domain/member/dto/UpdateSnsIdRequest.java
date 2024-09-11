package com.now_here5.now_here.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateSnsIdRequest {
    private final String snsId;
}
