package com.now_here5.now_here.domain.event.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class NewEventRequest {

    private final String field;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
    private final Long locationId;
}
