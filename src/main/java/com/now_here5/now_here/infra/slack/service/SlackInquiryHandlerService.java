package com.now_here5.now_here.infra.slack.service;

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
public class SlackInquiryHandlerService {
    @Value("${slack.inquiry.webhook.url}")
    private String inquiryWebhookUrl;
    private final RestTemplate restTemplate;

    public void sendSlackNotification(Long inquiryId, String inquiryContent, String sourceInfo) {
        HttpEntity<String> entity = getStringHttpEntity(inquiryId, inquiryContent, sourceInfo);

        ResponseEntity<String> response = restTemplate.exchange(inquiryWebhookUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.trace("Message sent to Slack successfully.");

        } else {
            log.error("Failed to send message to Slack. Status code: {}", response.getStatusCode());
            throw new RuntimeException("Failed to send message to Slack.");

        }
    }

    private HttpEntity<String> getStringHttpEntity(Long inquiryId, String inquiryContent, String sourceInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        String payload = String.format("{\"channel\": \"#운영-문의사항\", " +
                        "\"text\": \"*[문의사항]*\\n\\n" +
                        "*문의 ID :*  %d\\n" +
                        "*휴대폰  :*  %s\\n" +
                        "*내용      :*  %s\"}",
                inquiryId, sourceInfo, inquiryContent.replace("\n", "\\n").replace("\"", "\\\""));


        return new HttpEntity<>(payload, headers);
    }
}
