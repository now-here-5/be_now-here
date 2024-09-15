package com.now_here5.now_here.infra.notification.service;

import com.now_here5.now_here.infra.notification.config.SmsSender;
import com.now_here5.now_here.infra.notification.config.TwilioSmsSender;
import com.now_here5.now_here.infra.notification.dto.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final SmsSender smsSender;

    @Autowired
    public SmsService(@Qualifier("twilio") TwilioSmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public void sendSms(SmsRequest smsRequest) {
        smsSender.sendSms(smsRequest);
    }
}
