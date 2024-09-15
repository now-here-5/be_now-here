package com.now_here5.now_here.infra.notification.service;

import com.now_here5.now_here.global.util.RandomNumberUntil;
import com.now_here5.now_here.infra.notification.dto.SmsRequest;
import com.now_here5.now_here.infra.notification.repository.MemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Slf4j
@RequiredArgsConstructor
@Service
public class PhoneCodeService {
    private final MemoryRepository memoryRepository;
    private final SmsService smsService;

    public boolean sendVerificationCode(String phoneNumber) {
        try {
            String randomCode = RandomNumberUntil.generateRandomCode();
            log.info("send to phone number : {}, verification code : {}", phoneNumber, randomCode);

            memoryRepository.saveCheckCode(phoneNumber, randomCode); // 랜덤 번호, 이메일 저장.
            SmsRequest smsrequest = SmsRequest.builder().phoneNumber(phoneNumber).message(randomCode).build();
            smsService.sendSms(smsrequest);
            return true;

        } catch (Exception e) {
            log.error("Failed to send verification code: {}", e.getMessage());
            return false;
        }
    }


    public boolean verifyCode(String phoneNumber, String code) {

        String savedCode = memoryRepository.findCheckCodeBy(phoneNumber);

        if (savedCode != null && savedCode.equals(code)) {
            memoryRepository.saveCheckCode(phoneNumber, "true");
            return true;
        }
        return false;
    }

    public boolean isPhoneVerified(String phoneNumber) {

        try {
            boolean status = memoryRepository.checkStatusBy(phoneNumber);
            memoryRepository.delete(phoneNumber);
            return status;
        } catch (Exception e) {
            log.warn("Failed to check register code: {}", e.getMessage());
            return false;
        }
    }

    public String getPhoneCodeFromCacheMemory(String phoneNumber) {
        try {
            return memoryRepository.findCheckCodeBy(phoneNumber);
        } catch (Exception e) {
            log.warn("Failed to check register code: {}", e.getMessage());
            return null;
        }

    }

}
