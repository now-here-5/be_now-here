package com.now_here5.now_here.domain.event.service;

import com.now_here5.now_here.domain.event.converter.EventListToDto;
import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.dto.EventTimeResponse;
import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.event.repository.EventRepository;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventListToDto eventListToDto;
    private final AuthUtil authUtil;

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

    @Override
    public EventTimeResponse getEventTime() {
        try {
            AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
            if (authMember == null || authMember.getEvent() == null) {
                throw new IllegalStateException("Authenticated member 또는 event가 null입니다.");
            }

            Long eventId = authMember.getEvent().getEventId();
            Event event = eventRepository.getEventDetail(eventId);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime end = event.getPeriodEnd();
            if (end == null) {
                throw new IllegalStateException("Event end time이 null입니다.");
            }

            // 남은 시간 계산
            Duration duration = Duration.between(now, end);

            long days = duration.toDays();
            duration = duration.minusDays(days);
            long hours = duration.toHours();
            duration = duration.minusHours(hours);
            long minutes = duration.toMinutes();
            duration = duration.minusMinutes(minutes);
            long seconds = duration.getSeconds();

            return EventTimeResponse.builder()
                    .eventTime(String.format("%d일 %d시간 %d분 %d초", days, hours, minutes, seconds))
                    .build();

        } catch (Exception e) {
            log.error("이벤트 남은 시간을 얻는 중에 에러가 발생했습니다: {}", e.getMessage());
            return null;
        }
    }

}
