package com.now_here5.now_here.domain.event.converter;

import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.global.util.CustomXOR;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventListToDto {
    private final CustomXOR customXOR;
    public EventListResponse converter(List<Event> eventList) {
        return EventListResponse.builder()
                .eventList(
                        eventList.stream()
                                .map(event -> EventResponse.builder()
                                        .eventId(event.getId())
                                        .encodedId(customXOR.encrypt(event.getId()))
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
