package com.now_here5.now_here.infra.notification.service;

import com.now_here5.now_here.infra.notification.config.SmsSender;
import com.now_here5.now_here.infra.notification.config.TwilioSmsSender;
import com.now_here5.now_here.infra.notification.dto.SmsRequest;
import com.now_here5.now_here.infra.slack.service.SlackNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    private final SmsSender smsSender;
    private final SlackNotificationService slackNotificationService;

    @Autowired
    public SmsService(@Qualifier("twilio") TwilioSmsSender smsSender, SlackNotificationService slackNotificationService) {
        this.smsSender = smsSender;
        this.slackNotificationService = slackNotificationService;
        // 임시 방편
    }

    @Async
    public void sendSms(SmsRequest smsRequest) {
        // Uncomment the line below to actually send the SMS
        //        smsSender.sendSms(smsRequest);
        slackNotificationService.sendNotification("SMS sent to " + smsRequest.getPhoneNumber() + " with message: " + smsRequest.getMessage());
        log.info("Sending SMS to phone number: {}, with message: {}",
                smsRequest.getPhoneNumber(),
                smsRequest.getMessage());
    }
}
