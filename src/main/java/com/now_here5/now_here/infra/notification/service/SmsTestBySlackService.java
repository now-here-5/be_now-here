package com.now_here5.now_here.infra.notification.service;

import com.now_here5.now_here.infra.notification.dto.SmsRequest;
import com.now_here5.now_here.infra.slack.service.SlackNotificationService;
import com.now_here5.now_here.infra.slack.service.SlackNotificationSetupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
@Slf4j
public class SmsTestBySlackService implements NotificationService {

    private final SlackNotificationService slackNotificationService;

    public SmsTestBySlackService(SlackNotificationService slackNotificationService){
        this.slackNotificationService = slackNotificationService;
        log.info("sms service will be injected according to dev profile");
    }

    @Override
    public void sendSms(SmsRequest smsRequest) {
        slackNotificationService.sendSlackNotification(
                "핸드폰 번호: "+ smsRequest.getPhoneNumber()
                +"\n 내용: "+smsRequest.getMessage());
    }
}
