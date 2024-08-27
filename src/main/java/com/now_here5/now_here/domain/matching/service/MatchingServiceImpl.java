package com.now_here5.now_here.domain.matching.service;

import com.now_here5.now_here.domain.matching.converter.MatchingListToDto;
import com.now_here5.now_here.domain.matching.dto.BannerListResponse;
import com.now_here5.now_here.domain.matching.entity.Status;
import com.now_here5.now_here.domain.matching.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final MatchingRepository matchingRepository;
    private final MatchingListToDto matchingListToDto;

    @Cacheable("bannerListCache")// 메서드의 결과를 캐시하여 동일한 인자로 호출되면 캐시된 결과 반환
    @Override
    public List<BannerListResponse> getBannerList() {
        // 최적화된 쿼리로 데이터를 가져옴
        List<Object[]> results = matchingRepository.findMemberForBanner(Status.ACCEPTED);

        // 데이터를 DTO로 변환하여 반환
        return matchingListToDto.convertToBannerListResponse(results);
    }

    @Override
    public void notifyUser(Long memberId) {
        return ;
    }

    @Override
    public void clearNotifications(Long memberId) {
        return ;
    }
}
