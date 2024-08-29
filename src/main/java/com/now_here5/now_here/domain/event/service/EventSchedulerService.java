package com.now_here5.now_here.domain.event.service;


import com.now_here5.now_here.domain.event.dto.NewEventRequest;
import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.event.entity.Location;
import com.now_here5.now_here.domain.event.repository.EventRepository;
import com.now_here5.now_here.domain.event.repository.EventSchedulerRepository;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ScheduledFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class EventSchedulerService {
    private final TaskScheduler taskScheduler;
    private final EventRepository eventRepository;
    private final EventSchedulerRepository eventSchedulerRepository;
    private final MemberRepository memberRepository;
    private final CloseEventService closeEventService; // 새로운 서비스로 분리하여 트랜잭션 관리


    @Transactional
    public boolean openEvent(NewEventRequest newEventRequest){
        try {

            Location location = eventRepository.getLocationById(newEventRequest.getLocationId());

            Event event = Event.builder()
                    .field(newEventRequest.getField())
                    .status(true)
                    .periodEnd(newEventRequest.getEndsAt())
                    .periodStart(newEventRequest.getStartsAt())
                    .location(location)
                    .build();

            eventRepository.createEvent(event);

            // 트랜잭션 종료 후 스케줄링 작업을 설정
            // 이 작업은 트랜잭션 외부에서 실행되므로, 별도의 트랜잭션 관리가 필요
            scheduleEventClosure(event.getId(), event.getPeriodEnd());

            return true;
        } catch(Exception e) {
            log.error("Failed to create event: {}", e.getMessage());
            return false;
        }
    }

    // 이벤트 종료 스케줄링 작업을 설정하는 메서드
    public void scheduleEventClosure(Long eventId, LocalDateTime endsAt) {

        ZonedDateTime kstDateTime = endsAt.atZone(ZoneId.of("Asia/Seoul"));

        Instant instant = kstDateTime.toInstant();

        log.warn("Scheduling event closure at {}", kstDateTime);

        // TaskScheduler를 사용해 이벤트 종료 작업을 스케줄링
        // 이때, 별도의 서비스(CloseEventService)를 통해 트랜잭션을 관리
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> closeEventService.closeEventWithMembers(eventId), instant);
        eventSchedulerRepository.addEventScheduler(eventId, scheduledFuture); // 반환한 ScheduledFuture를 캐시에 저장
    }

    @Transactional
    public void closeEventWithMembers(Long eventId) {
        try {
            eventRepository.updateEventStatusById(eventId, false);
            memberRepository.deactivateBulkMembersByEventId(eventId);
            eventSchedulerRepository.removeEventScheduler(eventId);
        } catch(Exception e) {
            log.error("Failed to close event: {}", e.getMessage());
        }
    }

    @Transactional
    public boolean updateEvent(NewEventRequest newEventRequest, Long eventId){
        try {
            log.warn("locationId: {}", newEventRequest.getLocationId());
            eventRepository.getEventDetail(eventId);
            Event oldEvent = eventRepository.getEventDetail(eventId);
            oldEvent.replaceWithNewEvent(newEventRequest.getStartsAt(),
                    newEventRequest.getEndsAt(), true,
                    newEventRequest.getField(),
                    eventRepository.getLocationById(newEventRequest.getLocationId()));
            
            // 스케줄러 다시 생성
            eventSchedulerRepository.removeEventScheduler(eventId);
            scheduleEventClosure(eventId, newEventRequest.getEndsAt());
            
            return true;
        } catch (Exception e) {
            log.error("Failed to delete event: {}", e.getMessage());
            return false;
        }
    }

    // 스케줄러에 등록된 이벤트 키 목록을 반환하는 메서드
    public List<Long> getEventSchedulerKeys() {
        try {
            return eventSchedulerRepository.getEventSchedulerKeys();
        } catch (Exception e) {
            log.error("Failed to get event scheduler keys: {}", e.getMessage());
            return null;
        }
    }
}


@Service
@Slf4j
@RequiredArgsConstructor
class CloseEventService {

    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final EventSchedulerRepository eventSchedulerRepository;

    // 새로운 트랜잭션에서 이벤트를 종료하고 관련된 회원들을 비활성화하는 메서드
    // 이 메서드는 별도의 서비스로 분리해서 관리함. 아래와 같음.
    // 여기서 스케줄링된 작업에서 실행됨.
    // 트랜잭션 전파를 REQUIRES_NEW로 설정하여, 이 메서드가 호출될 때마다 새로운 트랜잭션이 시작되도록 함.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void closeEventWithMembers(Long eventId) {
        try {
            eventRepository.updateEventStatusById(eventId, false);
            memberRepository.deactivateBulkMembersByEventId(eventId);
            eventSchedulerRepository.removeEventScheduler(eventId);
        } catch(Exception e) {
            log.error("Failed to close event: {}", e.getMessage());
        }
    }
}

