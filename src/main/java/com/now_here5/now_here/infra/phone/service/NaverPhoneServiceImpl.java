package com.now_here5.now_here.infra.phone.service;


import com.now_here5.now_here.global.util.RandomNumberUntil;
import com.now_here5.now_here.infra.phone.repository.MemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaverPhoneServiceImpl implements PhoneService {
    private final MemoryRepository memoryRepository;

    @Override
    public boolean sendVerificationCode(String phone) {

        try{
            String randomCode= RandomNumberUntil.generateRandomCode();
            log.info("Send verification code to phone number: {}, code : {}", phone, randomCode);
            memoryRepository.saveCheckCode(phone, randomCode); // 랜덤 번호, 이메일 저장.

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
}
