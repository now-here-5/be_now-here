package com.now_here5.now_here.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class NewLocationRequest {
    private final String locationName;

    @JsonCreator
    public NewLocationRequest(@JsonProperty("locationName") String locationName) {
        this.locationName = locationName;
    }
}