package com.now_here5.now_here.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;


@Component
public class CustomXOR {
    @Value("${custom.xor.key}")
    private int KEY;

    // 암호화
    public String encrypt(Long number) {
        long encrypted = number ^ KEY; // XOR 연산
        return Base64.getEncoder().withoutPadding().encodeToString(Long.toString(encrypted).getBytes());
    }

    // 복호화
    public long decrypt(String encrypted) {
        String decoded = new String(Base64.getDecoder().decode(encrypted));
        return Long.parseLong(decoded) ^ KEY; // 다시 XOR 연산
    }

}
