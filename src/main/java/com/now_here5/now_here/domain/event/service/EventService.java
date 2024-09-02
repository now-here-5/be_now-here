package com.now_here5.now_here.domain.event.service;

import com.now_here5.now_here.domain.event.dto.*;

import java.util.List;

public interface EventService {

    EventResponse getEventDetail(Long eventId);

    EventListResponse getEventList(boolean status);

   EventTimeResponse getEventTime();

   boolean createLocation(NewLocationRequest newLocationRequest);

   List<LocationResponse> getLocationList();

   String  getLocation(Long locationId);

   boolean deleteLocation(Long locationId);
}
