package com.now_here5.now_here.infra.notification.config;

import com.twilio.Twilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwilioInitializer.class);

    @Autowired
    public TwilioInitializer(TwilioConfiguration twilioConfiguration) {
        Twilio.init(
                twilioConfiguration.getAccountSid(),
                twilioConfiguration.getAuthToken()
        );
        LOGGER.info("Twilio initialized ... with account sid {} ", twilioConfiguration.getAccountSid());
    }
}