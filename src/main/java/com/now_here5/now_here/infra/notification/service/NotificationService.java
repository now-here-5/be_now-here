package com.now_here5.now_here.infra.notification.service;


import com.now_here5.now_here.infra.notification.dto.SmsRequest;

public interface NotificationService {
    void sendSms(SmsRequest smsRequest);
}
