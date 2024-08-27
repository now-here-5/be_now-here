package com.now_here5.now_here.domain.matching.service;

import com.now_here5.now_here.domain.matching.dto.BannerListResponse;

import java.util.List;


public interface MatchingService {
    List<BannerListResponse> getBannerList();
    void notifyUser(Long memberId);
    void clearNotifications(Long memberId);
}
