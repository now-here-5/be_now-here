package com.now_here5.now_here.domain.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class EventTimeResponse {
    private final String eventTime;
}
