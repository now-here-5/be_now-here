package com.now_here5.now_here.infra.email.service;

import com.now_here5.now_here.infra.email.dto.EmailFormDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailSetupService {

    private final EmailSenderService emailSenderService;
    private final SpringTemplateEngine templateEngine;

    private static final String CODE_EMAIL_SUBJECT_POSTFIX = "이메일 인증코드";
    private static final String INQUIRY_EMAIL_SUBJECT_POSTFIX = "문의사항에 대한 답변";

    public void sendEmail(EmailFormDto emailForm, EmailContentType emailContentType) {
        String postfix = getPostfixByEmailContentType(emailContentType);

        try {
            String emailContent = generateEmailContent(emailForm, emailContentType);

            emailSenderService.sendHtmlTextEmail(
                    emailForm.getEmail() + postfix,
                    emailContent,
                    emailForm.getEmail()
            );
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String getPostfixByEmailContentType(EmailContentType emailContentType) {
        return emailContentType == EmailContentType.INQUIRY ?
                INQUIRY_EMAIL_SUBJECT_POSTFIX : CODE_EMAIL_SUBJECT_POSTFIX;
    }

    private String generateEmailContent(EmailFormDto emailForm, EmailContentType emailContentType) {
        Context context = new Context();

//        if (emailContentType == EmailContentType.INQUIRY) {
//            context.setVariable("emailType", "INQUIRY");
//            context.setVariable("inquiry", emailForm.getInquiry());
//            context.setVariable("answer", emailForm.getContent());
//        } else {
//            context.setVariable("emailType", "CODE");
//            context.setVariable("title", "인증코드 발송");
//            context.setVariable("code", emailForm.getContent());
//        }

        context.setVariable("inquiry", emailForm.getInquiry());
        context.setVariable("answer", emailForm.getContent());
        return templateEngine.process("emailTemplate", context);  // Thymeleaf 템플릿 처리
    }
}
