package com.now_here5.now_here.global.security.service;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final Long eventId;

    public CustomAuthenticationToken(Object principal, Object credentials, Long eventId) {
        super(principal, credentials);
        this.eventId = eventId;
    }

}
