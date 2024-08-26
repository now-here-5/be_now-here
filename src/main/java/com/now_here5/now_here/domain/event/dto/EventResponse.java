package com.now_here5.now_here.domain.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class EventResponse {
    private final Long eventId;
    private final String eventName;
    private final String location;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
    private final boolean status;
}
