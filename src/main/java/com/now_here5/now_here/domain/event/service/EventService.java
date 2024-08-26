package com.now_here5.now_here.domain.event.service;

import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;

public interface EventService {

    EventResponse getEventDetail(Long eventId);

    EventListResponse getEventList(boolean status);
}
