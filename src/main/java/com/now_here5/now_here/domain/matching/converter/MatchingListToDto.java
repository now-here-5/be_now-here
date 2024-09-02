package com.now_here5.now_here.domain.matching.converter;

import com.now_here5.now_here.domain.matching.dto.BannerListResponse;
import com.now_here5.now_here.domain.member.entity.MBTI;
import com.now_here5.now_here.domain.member.entity.Gender;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class MatchingListToDto {

    public List<BannerListResponse> convertToBannerListResponse(List<Object[]> results) {
        return results.stream()
                .map(result -> BannerListResponse.builder()
                        .senderNickname((String) result[0])
                        .senderMbti(result[1] instanceof MBTI ? ((MBTI) result[1]).name() : (String) result[1])
                        .senderGender(result[2] instanceof Gender ? ((Gender) result[2]).getValue() : (String) result[2])
                        .receiverNickname((String) result[3])
                        .receiverMbti(result[4] instanceof MBTI ? ((MBTI) result[4]).name() : (String) result[4])
                        .receiverGender(result[5] instanceof Gender ? ((Gender) result[5]).getValue() : (String) result[5])
                        .build())
                .collect(Collectors.toList());
    }
}