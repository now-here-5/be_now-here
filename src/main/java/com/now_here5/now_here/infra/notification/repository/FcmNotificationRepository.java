package com.now_here5.now_here.infra.notification.repository;

import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.global.util.AuthUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Repository
@Slf4j
public class FcmNotificationRepository implements NotificationRepository {
    private final EntityManager em;
    AuthUtil authUtil;
    private final MemberRepository memberRepository;


    @Transactional
    public void saveToken(String token, Member member) {
        try {
            member.setFcmToken(token);
            em.persist(member);
        } catch (Exception e) {
            log.error("Failed to save FCM token: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
