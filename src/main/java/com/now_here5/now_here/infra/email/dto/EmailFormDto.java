package com.now_here5.now_here.infra.email.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailFormDto {
    private final String email;
    private final String title;
    private final String answer;
    private final String inquiry;
}
