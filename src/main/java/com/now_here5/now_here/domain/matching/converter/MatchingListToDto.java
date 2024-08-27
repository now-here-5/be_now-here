package com.now_here5.now_here.domain.matching.converter;

import com.now_here5.now_here.domain.matching.dto.BannerListResponse;
import com.now_here5.now_here.domain.member.entity.MBTI;
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
                        .receiverNickname((String) result[2])
                        .receiverMbti(result[3] instanceof MBTI ? ((MBTI) result[3]).name() : (String) result[3])
                        .build())
                .collect(Collectors.toList());
    }

}