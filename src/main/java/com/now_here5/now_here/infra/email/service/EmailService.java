//package com.now_here5.now_here.infra.email.service;
//
//
//
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private static final String ENCODING_UTF8 = "UTF-8";
//
//    private final JavaMailSender javaMailSender;
//
//    @Async
//    public void sendHtmlTextEmail(String subject, String content, String email) {
//        final MimeMessage message = javaMailSender.createMimeMessage();
//        try {
//            final MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, ENCODING_UTF8);
//            messageHelper.setTo(email);
//            messageHelper.setSubject(subject);
//            messageHelper.setText(content, true);
//            javaMailSender.send(message);
//
//        } catch (Exception e) {
//            log.error("error on sending a mail : {}", e.getMessage()); // 예외 발생 시 로그 출력
//            throw new RuntimeException() ;
//        }
//    }
//
//}