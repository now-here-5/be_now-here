package com.now_here5.now_here.domain.member.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
@Getter
@Builder
@RequiredArgsConstructor
public class RegisterMemberRequest {
    private final String accountId;
    private final String snsId;
    private final String password;
    private final String nickname;
    private final LocalDate birth;
    private final String mbti;
    private final String gender;
    private final String description;
}