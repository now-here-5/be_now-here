package com.now_here5.now_here.infra.notification.config;

import com.now_here5.now_here.infra.notification.dto.SmsRequest;

public interface SmsSender {

    void sendSms(SmsRequest smsRequest);
}

