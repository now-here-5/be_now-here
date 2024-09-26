package com.now_here5.now_here.domain.event.service;

import com.now_here5.now_here.domain.event.converter.EventListToDto;
import com.now_here5.now_here.domain.event.dto.*;
import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.event.entity.Location;
import com.now_here5.now_here.domain.event.repository.EventRepository;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.util.AuthUtil;
import com.now_here5.now_here.global.util.CustomXOR;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventListToDto eventListToDto;
    private final AuthUtil authUtil;
    private final CustomXOR customXOR;

    @Transactional(readOnly = true)
    @Override
    public EventResponse getEventDetail(Long eventId) {

        try{
            Event event =  eventRepository.getEventDetail(eventId);
            return EventResponse.builder()
                    .eventId(event.getId())
                    .encodedId(customXOR.encrypt(event.getId()))
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

    @Transactional(readOnly = true)
    @Override
    public EventListResponse getEventList(boolean status, boolean isAdmin) {
        try{
            List<Event> eventList = eventRepository.getEventList(status);
            return eventListToDto.converter(eventList, isAdmin);
        }catch(Exception e){
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public EventTimeResponse getEventTime() {
        try {
            AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();

            if (authMember == null || authMember.getEventId() == null) {
                throw new IllegalStateException("Authenticated member 또는 event가 null입니다.");
            }

            LocalDateTime end = authMember.getEndsAt();
            if (end == null) {
                throw new IllegalStateException("Event end time이 null입니다.");
            }

            return EventTimeResponse.builder()
                    .eventTime(end)
                    .build();

        } catch (Exception e) {
            log.error("이벤트 남은 시간을 얻는 중에 에러가 발생했습니다: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    @Override
    public boolean createLocation(NewLocationRequest newLocationRequest) {
        try{
            Location location = Location.builder()
                    .locationName(newLocationRequest.getLocationName())
                    .build();
            eventRepository.createLocation(location);
            return true;
        }catch (Exception e){
            log.error("createLocation error = {} ", e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public  List<LocationResponse> getLocationList() {

        try{
            List<Location> locations =  eventRepository.getLocationList();
            return locations.stream()
                    .map(location -> LocationResponse.builder()
                            .locationId(location.getId())
                            .locationName(location.getLocationName())
                            .build())
                    .toList();

        }catch(Exception e){
            log.error("getLocationList error = {} ", e.getMessage());
//            return List.of();
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public String getLocation(Long locationId) {
        try{
            Location location = eventRepository.getLocationById(locationId);
            return location.getLocationName();
        }catch(Exception e){
            log.error("getLocation error = {} ", e.getMessage());
            return null;
        }
    }

    @Transactional
    @Override
    public boolean deleteLocation(Long locationId) {
        try{
            eventRepository.deleteLocationById(locationId);
            return true;
        }catch(Exception e){
            log.error("deleteLocation error = {} ", e.getMessage());
            return false;
        }
    }

}
