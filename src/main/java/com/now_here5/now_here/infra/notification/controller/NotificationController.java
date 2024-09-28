package com.now_here5.now_here.infra.notification.controller;

import com.now_here5.now_here.infra.notification.dto.SmsRequest;
import com.now_here5.now_here.infra.notification.service.SmsService;
import com.now_here5.now_here.infra.slack.service.SlackNotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "알림 API")
@RequestMapping("/notification")
public class NotificationController {

    private final SmsService smsService;
    private final SlackNotificationService slackNotificationService;

    @PostMapping
    public void sendSms(@Valid @RequestBody SmsRequest smsRequest) {
        // smsService.sendSms(smsRequest);
        slackNotificationService.sendNotification("send to "+ smsRequest.getPhoneNumber() + " : " + smsRequest.getMessage());
    }
}