package com.now_here5.now_here.domain.member.dto;


import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateBirthdayRequest {
    private LocalDate birthday;
}
