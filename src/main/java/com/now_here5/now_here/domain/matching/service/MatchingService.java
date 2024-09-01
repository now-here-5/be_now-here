package com.now_here5.now_here.domain.matching.service;

import com.now_here5.now_here.domain.matching.dto.*;

import java.util.List;


public interface MatchingService {
    List<BannerListResponse> getBannerList();
    void sendLove(Long receiverId);
    void receiveLove(Long senderId);

    List<SummaryResponse> getSummary();
    List<SenderResponse> getSenderList();

    List<ReceiverResponse> getReceiverList();

    List<SummaryDetailResponse> getAcceptedMatchings();

    List<NotificationResponse> getNotificationList();

    Integer getNotificationCount();
}
