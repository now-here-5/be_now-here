package com.now_here5.now_here.infra.slack.service;

import com.now_here5.now_here.global.logging.annotation.ExternalApiLogging;
import com.now_here5.now_here.infra.slack.dto.SlackNotificationType;
import com.now_here5.now_here.infra.slack.payload.FeedbackPayload;
import com.now_here5.now_here.infra.slack.payload.InquiryPayload;
import com.now_here5.now_here.infra.slack.payload.SimpleNotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SlackNotificationService {
    private final SlackNotificationSetupService slackNotificationSetupService;

    @ExternalApiLogging
    @Async
    public void sendSlackNotification(Long inquiryId, String inquiryContent, String sourceInfo) {
        InquiryPayload payload = new InquiryPayload(inquiryId, inquiryContent, sourceInfo);
        slackNotificationSetupService.sendNotification(payload, SlackNotificationType.INQUIRY);
    }

    @ExternalApiLogging
    @Async
    public void sendSlackNotification(Long feedId, Long memberId, String nickname, int rate, String content){
        FeedbackPayload payload = new FeedbackPayload(feedId, memberId, nickname, rate, content);
        slackNotificationSetupService.sendNotification(payload, SlackNotificationType.FEEDBACK);
    }

    @ExternalApiLogging
    @Async
    public void sendSlackNotification(String message){
        SimpleNotificationPayload payload = new SimpleNotificationPayload(message);
        slackNotificationSetupService.sendNotification(payload, SlackNotificationType.SIMPLE);
    }
}
