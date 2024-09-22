package com.now_here5.now_here.infra.slack.service;

import com.now_here5.now_here.global.logging.annotation.ExternalApiLogging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j

public class SlackNotificationService {
    @Value("${slack.notification.webhook.url}")
    private String webhookUrl;
    private final RestTemplate restTemplate;

    @ExternalApiLogging
    public void sendNotification(String message) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8"); // UTF-8로 메시지를 인코딩

        String payload = String.format("{\"text\": \"%s\"}", message);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Message sent to Slack successfully.");
        } else {
            log.error("Failed to send message to Slack: {}", response.getStatusCode());
        }
    }
}
