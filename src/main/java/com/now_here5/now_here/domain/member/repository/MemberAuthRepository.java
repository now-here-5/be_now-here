package com.now_here5.now_here.domain.member.repository;


import com.now_here5.now_here.domain.member.entity.ActiveMember;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MemberAuthRepository {

    private final EntityManager em;

    public boolean isValidToken(String token, Long userId) {
        try {
            ActiveMember member = em.find(ActiveMember.class, userId); // 토큰 검사 겸 멤버 영속성 컨텍스트에 저장

            if (member == null) {
                return false;
            }else{
                return member.getToken().equals(token);
            }

        } catch (Exception e) {
            log.error("isValidToken error ={}", e.getMessage());
            return false ; // 토큰이 유효하지 않은 것으로 간주.
        }
    }

    public void updateTokenById(String newToken, Long userId){
        try {
            ActiveMember member = em.find(ActiveMember.class, userId);  // 사용자 엔터티 조회
            if (member != null) {
                member.updateToken(newToken);
                em.merge(member);
            } else {
                log.error("User not found for ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("updateTokenForMember error ={}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public ActiveMember findMemberWithRolesByPhone(String phoneNumber){
        try {
            return em.createQuery("select am from ActiveMember am " +
                            "join fetch am.memberRoleList " +
                            "where am.phoneNumber = :phoneNumber", ActiveMember.class)
                    .setParameter("phoneNumber", phoneNumber)
                    .getSingleResult();
        } catch (Exception e) {
            log.warn("findMemberByPhone error ={}", e.getMessage());
            return null;
        }
    }
}
