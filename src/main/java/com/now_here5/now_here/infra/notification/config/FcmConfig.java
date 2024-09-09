package com.now_here5.now_here.infra.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class FcmConfig {

    // application.properties 또는 환경 변수에서 경로를 읽어옴
    @Value("${fcm.credentials.path}")
    private String serviceAccountKeyPath;

    @Value("${java.encode:false}")  // 기본값을 false로 설정
    private boolean isJavaEncode;

    @PostConstruct
    public void initialize() {
        try {
            if (isJavaEncode) {
                // 환경 변수에서 Base64 인코딩된 FCM 서비스 계정 키 가져오기
                String serviceAccountKeyBase64 = System.getenv("FCM_SERVICE_ACCOUNT_KEY");

                if (serviceAccountKeyBase64 != null) {
                    // Base64 디코딩 후 ByteArrayInputStream으로 변환
                    byte[] decodedBytes = java.util.Base64.getDecoder().decode(serviceAccountKeyBase64);
                    ByteArrayInputStream serviceAccountStream = new ByteArrayInputStream(decodedBytes);

                    // Firebase 옵션 설정
                    GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccountStream);
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(googleCredentials)
                            .build();

                    FirebaseApp.initializeApp(options);
                    log.info("FirebaseApp successfully initialized using Base64-encoded key.");
                } else {
                    throw new IllegalStateException("FCM Base64 인코딩된 서비스 계정 키를 찾을 수 없습니다.");
                }
            } else {
                // 파일 경로에서 Firebase 서비스 계정 키 로드
                if (serviceAccountKeyPath != null && !serviceAccountKeyPath.isEmpty()) {
                    // 파일을 읽어 Firebase 옵션 설정
                    FileInputStream serviceAccountStream = new FileInputStream(serviceAccountKeyPath);
                    GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccountStream);

                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(googleCredentials)
                            .build();

                    FirebaseApp.initializeApp(options);
                    log.info("FirebaseApp successfully initialized using local key file.");
                } else {
                    throw new IllegalStateException("FCM 서비스 계정 키 경로를 찾을 수 없습니다.");
                }
            }

        } catch (IOException e) {
            log.error("FCM 초기화 중 오류 발생: {}", e.getMessage());
        }
    }
}
