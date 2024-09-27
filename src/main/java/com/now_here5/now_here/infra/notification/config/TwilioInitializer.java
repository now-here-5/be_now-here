package com.now_here5.now_here.infra.notification.config;

import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TwilioInitializer {

    public TwilioInitializer(@Value("${twilio.account_sid}")
                             String accountSid,
                             @Value("${twilio.auth_token}")
                             String authToken) {

        Twilio.init(accountSid, authToken);
        log.info("Twilio initialized ");
    }
}