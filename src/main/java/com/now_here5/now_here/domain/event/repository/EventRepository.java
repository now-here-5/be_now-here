package com.now_here5.now_here.domain.event.repository;

import com.now_here5.now_here.domain.event.entity.Event;

import java.util.List;

public interface EventRepository {

    List<Event> getEventList(boolean status);

    Event getEventDetail(Long eventId);
}
