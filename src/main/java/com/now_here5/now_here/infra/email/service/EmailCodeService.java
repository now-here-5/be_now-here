//package com.now_here5.now_here.infra.email.service;
//
//
//import com.now_here5.now_here.global.util.RandomNumberUntil;
//import com.now_here5.now_here.infra.email.dto.EmailRequestDto;
//import com.now_here5.now_here.infra.phone.repository.MemoryRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class EmailCodeService {
//
//    private static final String REGISTER_EMAIL_SUBJECT_POSTFIX = " 님의 소중한 의견 감사합니다.!";
//
//    private final MemoryRepository memoryRepository;
//    private final EmailService emailService;
//    private String confirmEmailUI;
//
//    public boolean sendRegisterCode(EmailRequestDto emailRequestDto) {
//        try {
//            String randomCode= RandomNumberUntil.generateRandomCode();
//
//            emailService.sendHtmlTextEmail(
//                    emailRequestDto.getNickname() + REGISTER_EMAIL_SUBJECT_POSTFIX,
//                    getRegisterEmailText(emailRequestDto.getEmail(), randomCode),
//                    emailRequestDto.getEmail()); // 메일 전송
//
//            memoryRepository.saveCheckCode(emailRequestDto.getEmail(), randomCode); // 랜덤 번호, 이메일 저장.
//
//            return true;
//
//        }catch (Exception e){
//            log.error("Error sending email code: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    public boolean checkRegisterCode( String email, String code) {
//        final String savedCode = memoryRepository.findCheckCodeBy(email);
//
//        boolean result = (savedCode != null) && (savedCode.equals(code));
//        memoryRepository.delete(email);
//        return result;
//    }
//
//
//    private String getRegisterEmailText(String email, String code) {
//        return String.format(confirmEmailUI, email, code);
//    }
//
//
//    @PostConstruct
//    private void loadEmailUI() throws Exception {
//        try {
//            final ClassPathResource confirmEmailUIResource = new ClassPathResource("static/confirmEmailUI.html");
//            confirmEmailUI = new String(confirmEmailUIResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//
//        } catch (IOException e) {
//            log.error("Error loading email UI template: {}", e.getMessage()); // 예외 발생 시 로그 출력
//            throw new Exception("Error loading email UI template", e);
//        }
//    }
//}