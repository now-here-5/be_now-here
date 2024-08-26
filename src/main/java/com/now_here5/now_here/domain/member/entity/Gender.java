package com.now_here5.now_here.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("male"), FEMALE("female");

    private final String value;
}
