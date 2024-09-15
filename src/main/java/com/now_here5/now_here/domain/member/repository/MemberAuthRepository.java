package com.now_here5.now_here.domain.member.repository;


import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MemberAuthRepository {

    private final EntityManager em;

    @Transactional
    public Member findMemberByToken(String token) {
        try {
            return em.createQuery("select am from Member am " +
                            "join fetch am.event " +
                            "left join fetch am.memberRoleList " +
                            "left join fetch am.event.location " +
                            "where am.token = :token " +
                            "and am.active = true", Member.class) // 혹시 토큰을 만들더라도, 사용자가 탈퇴한 경우에는 토큰을 사용할 수 없도록 방지.
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("isValidToken error ={}, token ={}", e.getMessage(),token);
            return null ; // 토큰이 유효하지 않은 것으로 간주.
        }
    }

    @Transactional
    public void updateTokenById(String newToken, Long userId){
        try {
            Member member = em.find(Member.class, userId);  // 사용자 엔터티 조회
            if (member != null) {
                member.updateToken(newToken);
            } else {
                log.error("User not found for ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("updateTokenForMember error ={}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Member findMemberWithRolesByPhoneNumber(String phoneNumber, Long eventId){
        try {
            return em.createQuery("select am from Member am " +
                            "left join fetch am.memberRoleList " +
                            "join fetch am.event e " +
                            "join fetch e.location " +
                            "where am.phoneNumber = :phoneNumber " +
                            "and am.event.id = :eventId " +
                            "and am.active = true", Member.class) // 사용자가 탈퇴한 경우에는 로그인을 할 수 없도록 방지.
                    .setParameter("phoneNumber", phoneNumber)
                    .setParameter("eventId", eventId)
                    .getSingleResult();
        } catch (Exception e) {
            log.warn("findMemberByPhone error ={}", e.getMessage());
            return null;
        }
    }

}
