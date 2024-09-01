package com.now_here5.now_here.infra.slack.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlackNotificationServiceTest {
    @Value("${slack.inquiry.webhook.url}")
    private static String WEBHOOK_URL;
    private RestTemplate restTemplate;
    private SlackNotificationService slackNotificationService;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        slackNotificationService = new SlackNotificationService(restTemplate);
        ReflectionTestUtils.setField(slackNotificationService, "webhookUrl", WEBHOOK_URL);
    }

    @Test
    void sendNotification() {
        // Arrange
        String testMessage = "[Now, here] 매칭 알림 \n 매칭 성공! \n 왕밤빵과 매칭되었어요!";
        String expectedPayload = String.format("{\"text\": \"%s\"}", testMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");

        // Act
        slackNotificationService.sendNotification(testMessage);

        // 실제로 메시지가 전송되었는지 확인하려면 아래 코드를 사용
        ResponseEntity<String> response = restTemplate.exchange(WEBHOOK_URL, HttpMethod.POST, new HttpEntity<>(expectedPayload, headers), String.class);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        System.out.println("Response: " + response.getBody());
    }
}
