package com.now_here5.now_here.infra.email.service;

import com.now_here5.now_here.infra.email.dto.EmailFormDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailInquiryService {

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private static final String INQUIRY_EMAIL_SUBJECT_POSTFIX = "문의사항에 대한 답변";

    public void setUpEamilAndSendEmail(String email, String inquiry, String answer){
        try{
            EmailFormDto emailForm = EmailFormDto.builder()
                    .email(email)
                    .inquiry(inquiry)
                    .answer(answer)
                    .build();

            this.setUpEamilAndSendEmail(emailForm);
        }catch(Exception e){
            log.error("Failed to send the answer email for inquiry: {}", e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private void setUpEamilAndSendEmail(EmailFormDto emailForm) {
        try {
            String emailContent = generateEmailContent(emailForm);

            emailService.sendHtmlTextEmail(
                    emailForm.getEmail() + INQUIRY_EMAIL_SUBJECT_POSTFIX,
                    emailContent,
                    emailForm.getEmail()
            );
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String generateEmailContent(EmailFormDto emailForm) {
        Context context = new Context();

        context.setVariable("inquiry", emailForm.getInquiry());
        context.setVariable("answer", emailForm.getAnswer());
        return templateEngine.process("emailTemplate", context);  // Thymeleaf 템플릿 처리
    }
}
