package com.now_here5.now_here.infra.slack.service;

import com.now_here5.now_here.domain.interaction.service.InteractionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackEventService {

    private final InteractionService interactionService;

    public String processSlackEvent(Map<String, Object> payload) {
        EventDetails eventDetails = parseEventDetails(payload);

        if (eventDetails != null && "app_mention".equals(eventDetails.getType())) {
            log.info("Received a mention from user {} in channel {}, text : {}",
                    eventDetails.getUser(), eventDetails.getChannelId(), eventDetails.getText());

            // 명령어를 파싱하는 메서드를 호출
            ParsedCommand parsedCommand = parseCommand(eventDetails.getText());

            if (parsedCommand != null) {
                // 서버로 응답 내용을 전송하여 SMS로 답변 처리
                interactionService.processInquiryResponse(parsedCommand.getInquiryId(), parsedCommand.getAnswer());
                return "OK";
            } else {
                log.error("Failed to parse command from text: {}", eventDetails.getText());
                return "Error: Invalid command format.";
            }
        }
        return "OK";
    }

    // EventDetails 객체로 파싱하는 메서드
    private EventDetails parseEventDetails(Map<String, Object> payload) {
        try {
            String eventType = (String) payload.get("type");

            if ("event_callback".equals(eventType)) {
                Map<String, Object> event = (Map<String, Object>) payload.get("event");
                String type = (String) event.get("type");
                String channelId = (String) event.get("channel");
                String user = (String) event.get("user");
                String text = (String) event.get("text");

                return new EventDetails(eventType, type, channelId, user, text);
            }
        } catch (ClassCastException e) {
            log.error("Failed to parse event details from payload: {}", payload, e);
        }
        return null; // 파싱 실패 시 null 반환
    }

    // 명령어를 파싱하는 메서드
    private ParsedCommand parseCommand(String text) {
        try {
            // "/answer :"을 기준으로 메시지를 나눔
            if (text.contains("/answer :")) {
                String[] parts = text.split("/answer :");

                if (parts.length >= 2) {
                    String[] inquiryAndAnswer = parts[1].split("\n", 2); // 첫 번째 줄에서 inquiryId를, 두 번째 줄에서 답변을 추출

                    if (inquiryAndAnswer.length >= 2) {
                        Long inquiryId = Long.valueOf(inquiryAndAnswer[0].trim()); // inquiryId를 공백 제거 후 숫자로 변환
                        String answer = inquiryAndAnswer[1].trim(); // 답변 내용의 앞뒤 공백 제거
                        return new ParsedCommand(inquiryId, answer);
                    }
                }
            }
        } catch (NumberFormatException e) {
            log.error("Failed to parse inquiryId from text: {}", text, e);
        }
        return null; // 파싱에 실패한 경우 null 반환
    }

    @Getter
    @RequiredArgsConstructor
    private static class EventDetails {
        private final String eventType;
        private final String type;
        private final String channelId;
        private final String user;
        private final String text;
    }

    @Getter
    @RequiredArgsConstructor
    private static class ParsedCommand {
        private final Long inquiryId;
        private final String answer;
    }
}
