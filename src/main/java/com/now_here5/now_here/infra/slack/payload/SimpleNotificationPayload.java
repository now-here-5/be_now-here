package com.now_here5.now_here.infra.slack.payload;

public class SimpleNotificationPayload implements SlackMessagePayload {

    private final String message;

    public SimpleNotificationPayload(String message) {
        this.message = message;
    }

    @Override
    public String getFormattedMessage() {
        return String.format("{\"text\": \"%s\"}", message);
    }
}

