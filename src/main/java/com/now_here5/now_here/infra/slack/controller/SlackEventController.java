package com.now_here5.now_here.infra.slack.controller;

import com.now_here5.now_here.infra.slack.service.SlackEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/slack/event")
@RequiredArgsConstructor
@Slf4j
public class SlackEventController {

    private final SlackEventService slackEventService;

    @PostMapping("/handle-inquiry")
    public ResponseEntity<String> handleSlackEvent(@RequestBody Map<String, Object> payload) {
        try {
            // Slack의 URL verification 요청을 처리
            if ("url_verification".equals(payload.get("type"))) {
                String challenge = (String) payload.get("challenge");
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(challenge);
            }

            // 다른 이벤트 처리 로직
            return ResponseEntity.ok(slackEventService.processSlackEvent(payload));

        } catch (Exception e) {
            log.error("An unexpected error occurred while handling Slack event.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: An unexpected error occurred.");
        }
    }
}
