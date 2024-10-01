package com.now_here5.now_here.infra.slack.service;

import com.now_here5.now_here.infra.slack.dto.SlackNotificationType;
import com.now_here5.now_here.infra.slack.payload.SlackMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackNotificationSetupService {

    @Value("${slack.feedback.webhook.url}")
    private String feedbackWebhookUrl;

    @Value("${slack.notification.webhook.url}")
    private String notificationWebhookUrl;

    @Value("${slack.inquiry.webhook.url}")
    private String inquiryWebhookUrl;

    private final RestTemplate restTemplate;

    public void sendNotification(SlackMessagePayload payload, SlackNotificationType type) {
        String webhookUrl = getWebhookUrl(type);
        HttpEntity<String> entity = createPayload(payload);
        sendToSlack(webhookUrl, entity);
    }

    private String getWebhookUrl(SlackNotificationType type) {
        return switch (type) {
            case FEEDBACK -> feedbackWebhookUrl;
            case SIMPLE -> notificationWebhookUrl;
            case INQUIRY -> inquiryWebhookUrl;
        };
    }

    private HttpEntity<String> createPayload(SlackMessagePayload payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        return new HttpEntity<>(payload.getFormattedMessage(), headers);
    }

    private void sendToSlack(String url, HttpEntity<String> entity) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Message sent successfully to Slack.");
            } else {
                log.error("Failed to send message to Slack: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error while sending message to Slack", e);
        }
    }
}


