package com.now_here5.now_here.infra.slack.controller;

import com.now_here5.now_here.domain.interaction.service.InteractionService;
import com.now_here5.now_here.infra.slack.service.SlackEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public String handleSlackEvent(@RequestBody Map<String, Object> payload) {
        try {
            return slackEventService.processSlackEvent(payload);
        } catch (Exception e) {
            log.error("An unexpected error occurred while handling Slack event.", e);
            return "Error: An unexpected error occurred.";
        }
    }
}
