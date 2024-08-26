package com.now_here5.now_here.infra.email.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailRequestDto {
        private final String email;
        private final String nickname;
}
