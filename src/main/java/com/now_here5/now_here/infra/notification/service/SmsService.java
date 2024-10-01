package com.now_here5.now_here.infra.notification.service;

import com.now_here5.now_here.infra.notification.config.SmsSender;
import com.now_here5.now_here.infra.notification.config.TwilioSmsSender;
import com.now_here5.now_here.infra.notification.dto.SmsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("prod")
public class SmsService implements NotificationService{

    private final SmsSender smsSender;

    @Autowired
    public SmsService(@Qualifier("twilio") TwilioSmsSender smsSender) {
        this.smsSender = smsSender;
        log.info("sms service will be injected according to prod profile");
    }

    @Async
    public void sendSms(SmsRequest smsRequest) {
        smsSender.sendSms(smsRequest);
        log.info("Sending SMS to phone number: {}, with message: {}",
                smsRequest.getPhoneNumber(),
                smsRequest.getMessage());
    }
}
