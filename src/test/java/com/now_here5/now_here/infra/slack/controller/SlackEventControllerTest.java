package com.now_here5.now_here.infra.slack.controller;

import com.now_here5.now_here.infra.slack.service.SlackEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SlackEventControllerTest {

    @Mock
    private SlackEventService slackEventService;

    @InjectMocks
    private SlackEventController slackEventController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleSlackEvent_Success() {
        // Given
        Map<String, Object> payload = new HashMap<>();
        when(slackEventService.processSlackEvent(payload)).thenReturn("OK");

        // When
        String response = slackEventController.handleSlackEvent(payload);

        // Then
        assertEquals("OK", response);
        verify(slackEventService, times(1)).processSlackEvent(payload);
    }

    @Test
    void handleSlackEvent_ShouldHandleException() {
        // Given
        Map<String, Object> payload = new HashMap<>();
        when(slackEventService.processSlackEvent(payload)).thenThrow(new RuntimeException("Unexpected Error"));

        // When
        String response = slackEventController.handleSlackEvent(payload);

        // Then
        assertEquals("Error: An unexpected error occurred.", response);
        verify(slackEventService, times(1)).processSlackEvent(payload);
    }
}
