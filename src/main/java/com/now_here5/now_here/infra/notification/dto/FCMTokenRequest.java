package com.now_here5.now_here.infra.notification.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FCMTokenRequest {

    private String token;
    private String memberId;
}
