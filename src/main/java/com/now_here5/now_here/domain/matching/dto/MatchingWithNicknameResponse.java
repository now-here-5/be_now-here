package com.now_here5.now_here.domain.matching.dto;

import com.now_here5.now_here.domain.matching.entity.Matching;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingWithNicknameResponse {
    private Matching matching;
    private String counterpartNickname; // 상대방의 닉네임
}