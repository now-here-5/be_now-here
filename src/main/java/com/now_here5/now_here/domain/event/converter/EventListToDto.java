package com.now_here5.now_here.domain.event.converter;

import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.entity.Event;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class EventListToDto {
    public EventListResponse converter(List<Event> eventList) {
        return EventListResponse.builder()
                .eventList(
                        eventList.stream()
                                .map(event -> EventResponse.builder()
                                        .eventId(event.getId())
                                        .eventName(event.getField())
                                        .location(event.getLocation().getLocationName())
                                        .status(event.isStatus())
                                        .startsAt(event.getPeriodStart())
                                        .endsAt(event.getPeriodEnd())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }
}
