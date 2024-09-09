package com.now_here5.now_here.infra.email.service;

import com.now_here5.now_here.global.util.RandomNumberUntil;
import com.now_here5.now_here.infra.email.dto.EmailFormDto;
import com.now_here5.now_here.infra.email.repository.MemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Slf4j
@RequiredArgsConstructor
@Service
public class EmailCodeService {
    private final MemoryRepository memoryRepository;
    private final EmailSetupService emailSetupService;

    public boolean sendVerificationCode(String email) {
        try {
            String randomCode = RandomNumberUntil.generateRandomCode();
            log.info("send to email: {}, verification code : {}", email, randomCode);
            EmailFormDto emailForm = EmailFormDto.builder()
                    .email(email)
                    .content(randomCode)
                    .build();
            emailSetupService.sendEmail(emailForm, EmailContentType.CODE);
            memoryRepository.saveCheckCode(email, randomCode); // 랜덤 번호, 이메일 저장.
            return true;
        } catch (Exception e) {
            log.error("Failed to send verification code: {}", e.getMessage());
            return false;
        }
    }


    public boolean verifyCode(String email, String code) {

        String savedCode = memoryRepository.findCheckCodeBy(email);

        if (savedCode != null && savedCode.equals(code)) {
            memoryRepository.saveCheckCode(email, "true");
            return true;
        }
        return false;
    }

    public boolean isVerifiedEmail(String email) {

        try {
            boolean status = memoryRepository.checkStatusBy(email);

            memoryRepository.delete(email);
            return status;
        } catch (Exception e) {
            log.warn("Failed to check register code: {}", e.getMessage());
            return false;
        }
    }

    public String getEmailCode(String email) {
        try {
            return memoryRepository.findCheckCodeBy(email);
        } catch (Exception e) {
            log.warn("Failed to check register code: {}", e.getMessage());
            return null;
        }

    }

}
