package com.now_here5.now_here.domain.event.repository;

import com.now_here5.now_here.domain.event.dto.NewEventRequest;
import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.event.entity.Location;

import java.util.List;

public interface EventRepository {

    List<Event> getEventList(boolean status);

    Event getEventDetail(Long eventId);

    List<Event> getSignedEventsByMember(boolean active, Long memberId);

    void updateEventStatusById(Long eventId, boolean status);

    void createEvent(Event event);

    void createLocation(Location location);

    Location getLocationById(Long locationId);

    List<Location> getLocationList();

    void deleteLocationById(Long locationId);

}
