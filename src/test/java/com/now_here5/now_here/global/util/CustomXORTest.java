package com.now_here5.now_here.global.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CustomXORTest {

    private CustomXOR customXOR;

    @BeforeEach
    void setUp() {
        customXOR = new CustomXOR();
    }

    @Test
    void encrypt() {
        long originalNumber = 1L;

        String encrypted = customXOR.encrypt(originalNumber);

        // 암호화된 문자열이 null이 아니고 비어있지 않은지 확인
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());

        System.out.println("Encrypted value: " + encrypted);
    }

    @Test
    void decrypt() {
        long originalNumber = 1;

        // 먼저 암호화
        String encrypted = customXOR.encrypt(originalNumber);

        // 복호화
        long decrypted = customXOR.decrypt(encrypted);

        // 복호화된 값이 원래 숫자와 같은지 확인
        assertEquals(originalNumber, decrypted);

        System.out.println("Decrypted value: " + decrypted);
    }
}
