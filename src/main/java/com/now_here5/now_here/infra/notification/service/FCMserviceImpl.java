package com.now_here5.now_here.infra.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.global.util.AuthUtil;
import com.now_here5.now_here.infra.notification.dto.NotificationRequestDto;
import com.now_here5.now_here.infra.notification.repository.FcmNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMserviceImpl implements FCMNotificationService {

    AuthUtil authUtil;
    private final FcmNotificationRepository fcmNotificationRepository;
    private final MemberRepository memberRepository;

    @Override
    public void sendMessages(NotificationRequestDto notificationRequestDto) throws ExecutionException, InterruptedException {
        Message message = Message.builder()
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(notificationRequestDto.getTitle())
                                .setBody(notificationRequestDto.getMessage())
                                .build())
                        .build())
                .setToken(notificationRequestDto.getToken())
                .build();
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        log.info(">>>>Send message : " + response);
    }

    @Override
    @Transactional
    public boolean saveFCMToken(String token, String memberId) {
        {
            try {
                Member user = memberRepository.findActiveMemberById(Long.valueOf(memberId));
                fcmNotificationRepository.saveToken(token, user);
                return true;
            } catch (Exception e) {
                log.error("Failed to update description: {}", e.getMessage());
                return false;
            }
        }
    }
}
