package com.now_here5.now_here.domain.event.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter

public class LocationResponse {
    private final Long locationId;
    private final String locationName;
}
