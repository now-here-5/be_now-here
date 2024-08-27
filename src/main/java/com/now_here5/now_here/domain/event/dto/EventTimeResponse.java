package com.now_here5.now_here.domain.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@Builder
@RequiredArgsConstructor
public class EventTimeResponse {
    private final String eventTime;
}
