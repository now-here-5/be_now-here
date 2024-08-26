package com.now_here5.now_here.domain.event.controller;


import com.now_here5.now_here.domain.event.dto.EventResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.service.EventService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    @GetMapping("/list")
    public ResponseEntity<ResponseForm> getEventList(
            @RequestParam(name = "status", required = true) boolean status){

        EventListResponse eventList = eventService.getEventList(status);

        return eventList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_SUCCESS, eventList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_FAIL));
    }

    @GetMapping("/detail/{event_id}")
    public ResponseEntity<ResponseForm> getSingleEvent(
            @PathVariable(name = "event_id", required = true ) Long eventId){

        EventResponse eventDetail = eventService.getEventDetail(eventId);

        return eventDetail != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_SUCCESS, eventDetail)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_QUERY_FAIL));
    }
}
