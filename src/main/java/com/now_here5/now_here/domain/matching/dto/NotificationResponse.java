package com.now_here5.now_here.domain.matching.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationResponse {
    private String title;
    private String memberName;
    private String content;

    @Builder
    public NotificationResponse(String title, String memberName, String content) {
        this.title = title;
        this.memberName = memberName;
        this.content = content;
    }
}