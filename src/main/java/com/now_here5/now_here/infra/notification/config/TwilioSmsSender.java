package com.now_here5.now_here.infra.notification.config;

import com.now_here5.now_here.global.logging.annotation.ExternalApiLogging;
import com.now_here5.now_here.infra.notification.dto.SmsRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("twilio")
@Slf4j
@RequiredArgsConstructor
public class TwilioSmsSender implements SmsSender {

    @Value("${twilio.phone_number}")
    private String twilioNumber;

    @Override
    @ExternalApiLogging
    public void sendSms(SmsRequest smsRequest) {
        String phoneNumber = smsRequest.getPhoneNumber();
        String messageContent = smsRequest.getMessage();

        // 전화번호 유효성 검사
        if (isPhoneNumberValid(phoneNumber)) {
            try {
                // Twilio API 사용하여 SMS 전송
                PhoneNumber to = new PhoneNumber(phoneNumber);
                PhoneNumber from = new PhoneNumber(twilioNumber);
                MessageCreator creator = Message.creator(to, from, messageContent);
                Message message = creator.create();

                // 성공 로그
                log.info("SMS sent successfully to {}. Message SID: {}", phoneNumber, message.getSid());
            } catch (Exception e) {
                // 예외 처리 및 오류 로그
                log.error("Failed to send SMS to {}. Error: {}", phoneNumber, e.getMessage());
                throw new RuntimeException("Failed to send SMS", e);
            }
        } else {
            // 유효하지 않은 전화번호 처리
            log.warn("Invalid phone number: {}", phoneNumber);
            throw new IllegalArgumentException(
                    "Phone number [" + phoneNumber + "] is not a valid number"
            );
        }
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        // 전화번호 형식이 +로 시작하고 국가 코드를 포함하는지 확인 (여기서는 +82로 시작하는 한국 번호만 처리)
        return phoneNumber.matches("\\+82\\d{9,10}");
    }
}
