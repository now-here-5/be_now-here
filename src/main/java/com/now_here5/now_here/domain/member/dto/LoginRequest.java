package com.now_here5.now_here.domain.member.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@Builder
@RequiredArgsConstructor
public class LoginRequest {

    private final String phoneNumber;

    private final String password;

}
