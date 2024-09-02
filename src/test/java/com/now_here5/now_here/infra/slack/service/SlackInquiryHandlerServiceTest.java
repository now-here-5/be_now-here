package com.now_here5.now_here.infra.slack.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
        "slack.inquiry.webhook.url=your-url"
})
class SlackInquiryHandlerServiceTest {

    @Value("${slack.inquiry.webhook.url}")
    private String inquiryWebhookUrl;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        new SlackInquiryHandlerService(restTemplate);
    }

    @Test
    void sendSlackNotification_RealRequest() {
        // Given
        Long inquiryId = 1L;
        String inquiryContent = "This is a test inquiry content.";
        String sourceInfo = "Test Source Info";

        // When
        ResponseEntity<String> response = sendRequest(inquiryId, inquiryContent, sourceInfo);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        System.out.println("Response from Slack: " + response.getBody());
    }

    private ResponseEntity<String> sendRequest(Long inquiryId, String inquiryContent, String sourceInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");

        String payload = String.format("{\"channel\": \"#your-channel\", \"text\": \"[Inquiry]\\n" +
                        "*Inquiry ID:* %d\\n" +
                        "*Source Info:* %s\\n" +
                        "*Content:* %s\"}",
                inquiryId, sourceInfo, inquiryContent);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(inquiryWebhookUrl, HttpMethod.POST, entity, String.class);

        // Handle redirection if necessary
        if (response.getStatusCodeValue() == 302) {
            String redirectUrl = Objects.requireNonNull(response.getHeaders().getLocation()).toString();
            response = restTemplate.exchange(redirectUrl, HttpMethod.POST, entity, String.class);
        }

        return response;
    }
}