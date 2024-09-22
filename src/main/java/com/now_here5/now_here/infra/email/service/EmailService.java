package com.now_here5.now_here.infra.email.service;

import com.now_here5.now_here.global.logging.annotation.ExternalApiLogging;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailService {

    private static final String ENCODING_UTF8 = "UTF-8";
    private final JavaMailSender javaMailSender;

    @ExternalApiLogging
    @Async
    public void sendHtmlTextEmail(String subject, String content, String recipientEmail) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, ENCODING_UTF8);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            javaMailSender.send(message);

        } catch (Exception e) {
            log.error("Error while sending an email to {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }


}
