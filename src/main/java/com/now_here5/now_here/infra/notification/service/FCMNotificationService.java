package com.now_here5.now_here.infra.notification.service;


import com.now_here5.now_here.infra.notification.dto.NotificationRequestDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

public interface FCMNotificationService {

    void sendMessages(NotificationRequestDto notificationRequestDto) throws ExecutionException, InterruptedException;

    @Transactional
    boolean saveFCMToken(String token, String memberId);
}
