package com.now_here5.now_here.global.security.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenDto {
    private final String token;
}
