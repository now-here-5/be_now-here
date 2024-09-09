package com.now_here5.now_here.infra.email.service;

import com.now_here5.now_here.infra.email.dto.EmailFormDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailInquiryService {
    private final EmailSetupService emailSetupService;
    public void sendEmail(String email, String inquiry, String answer){
        try{
            EmailFormDto emailForm = EmailFormDto.builder()
                    .email(email)
                    .inquiry(inquiry)
                    .content(answer)
                    .build();
            emailSetupService.sendEmail(emailForm, EmailContentType.INQUIRY);
        }catch(Exception e){
            log.error("Failed to send the answer email for inquiry: {}", e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
