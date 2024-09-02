package com.now_here5.now_here.infra.notification.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.now_here5.now_here.global.util.RandomNumberUntil;
import com.now_here5.now_here.infra.notification.repository.MemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaverNotificationServiceImpl implements NotificationService {
    private final MemoryRepository memoryRepository;

    @Override
    public boolean sendVerificationCode(String phone) {
        try{
            String randomCode= RandomNumberUntil.generateRandomCode();
            log.info("Send verification code to notification number: {}, code : {}", phone, randomCode);
            memoryRepository.saveCheckCode(phone, randomCode); // 랜덤 번호, 이메일 저장.
            // 실제로 sms 보내는 로직을 추가.
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public boolean verifyCode(String phone, String code) {

        String savedCode = memoryRepository.findCheckCodeBy(phone);

        if(savedCode != null && savedCode.equals(code)){
            memoryRepository.saveCheckCode(phone, "true");
            return true;
        }

        return false;
    }

    @Override
    public boolean isVerifiedPhone(String phone) {

        try{
            boolean status =  memoryRepository.checkStatusBy(phone);

            memoryRepository.delete(phone);
            return status;
        }catch(Exception e){
            log.warn("Failed to check register code: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getPhoneCode(String phone) {
        try{
            return memoryRepository.findCheckCodeBy(phone);
        }catch(Exception e){
            log.warn("Failed to check register code: {}", e.getMessage());
            return null;
        }

    }

    @Override
    public void sendSms(String phone, Object dto) {

        try {
            // dto를 JSON 문자열로 변환
            String jsonMessage = new ObjectMapper().writeValueAsString(dto);
            log.info("Send SMS to notification number: {}, message: {}", phone, jsonMessage);

            // 예: naverSmsApi.send(notification, jsonMessage);

        } catch (JsonProcessingException e) {
            log.error("Failed to convert dto to JSON: {}", dto, e);
        }
        log.info("Send SMS to notification number: {}, message : {}", phone, dto);

    }
}
