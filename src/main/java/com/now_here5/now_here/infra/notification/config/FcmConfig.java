package com.now_here5.now_here.infra.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
@Slf4j
public class FcmConfig {

    // application.properties에서 경로를 읽어옴
    @Value("${fcm.credentials.path}")
    private String serviceAccountKeyPath;

    @PostConstruct
    public void initialize() {
        try {
            // 경로가 있는지 확인하고 Firebase 옵션 설정
            if (serviceAccountKeyPath != null && !serviceAccountKeyPath.isEmpty()) {
                // 파일을 읽어 Firebase 옵션 설정
                FileInputStream serviceAccountStream = new FileInputStream(serviceAccountKeyPath);

                GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccountStream);

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(googleCredentials)
                        .build();

                FirebaseApp.initializeApp(options);

                log.info("FirebaseApp successfully initialized.");
            } else {
                throw new IllegalStateException("FCM 서비스 계정 키 경로를 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            log.error("FCM 초기화 중 오류 발생: {}", e.getMessage());
        }
    }
}
