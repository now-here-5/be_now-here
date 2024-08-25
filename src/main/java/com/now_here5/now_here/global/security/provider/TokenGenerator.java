package com.now_here5.now_here.global.security.provider;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Configuration
public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    private String generateToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes); // 랜덤 바이트 생성
        return base64Encoder.encodeToString(randomBytes); // 바이트를 문자열로 인코딩
    }

    public String generateUniqueToken() {
        String salt = UUID.randomUUID().toString();  // 고유한 UUID 생성 : 매우 낮은 확률로 중복.
        return generateToken() + "-" + salt;  // 토큰과 고유한 식별자 결합
    }
}
