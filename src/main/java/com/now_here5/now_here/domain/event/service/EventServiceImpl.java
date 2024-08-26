package com.now_here5.now_here.domain.event.service;

import com.now_here5.now_here.domain.event.converter.EventListToDto;
import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventListToDto eventListToDto;

    @Override
    public EventResponse getEventDetail(Long eventId) {

        try{
            Event event =  eventRepository.getEventDetail(eventId);
            return EventResponse.builder()
                    .eventId(event.getId())
                    .eventName(event.getField())
                    .location(event.getLocation().getLocationName())
                    .status(event.isStatus())
                    .startsAt(event.getPeriodStart())
                    .endsAt(event.getPeriodEnd())
                    .build();
        }catch(Exception e){
            return null;
        }
    }

    @Override
    public EventListResponse getEventList(boolean status) {
        try{
            List<Event> eventList = eventRepository.getEventList(status);
            return eventListToDto.converter(eventList);
        }catch(Exception e){
            return null;
        }
    }
}
