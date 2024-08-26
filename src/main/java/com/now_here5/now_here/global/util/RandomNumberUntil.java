package com.now_here5.now_here.global.util;
import java.util.Random;


public class RandomNumberUntil {
    private static final int REGISTER_CODE_LENGTH = 6;
    public static String generateRandomCode() {
        Random random = new Random(System.currentTimeMillis()); // 시드를 현재 시간으로 설정
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < REGISTER_CODE_LENGTH; i++) {
            int randomNumber = random.nextInt(10); // 0부터 9까지의 랜덤한 숫자 생성
            code.append(randomNumber);
        }

        return code.toString();
    }
}
