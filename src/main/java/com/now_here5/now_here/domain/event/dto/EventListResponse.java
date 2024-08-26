package com.now_here5.now_here.domain.event.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter

public class EventListResponse {
    private final List<EventResponse> eventList;
}
