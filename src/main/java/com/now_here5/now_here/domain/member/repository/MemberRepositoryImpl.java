package com.now_here5.now_here.domain.member.repository;

import com.now_here5.now_here.domain.member.entity.ActiveMember;
import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MemberRepositoryImpl implements MemberRepository {
    private final EntityManager em;

    @Override
    public List<ActiveMember> findActiveMemberByPhone(String phoneNumber) {
        try{
            return em.createQuery("select am from ActiveMember am " +
                            "join fetch am.event " +
                            "where am.phoneNumber = :phoneNumber " +
                            "and am.status = true", ActiveMember.class)
                    .setParameter("phoneNumber", phoneNumber)

                    .getResultList();
        }catch (Exception e){
            log.error("Failed to find active member by phone number: {}", phoneNumber);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean inactiveMember(Long memberId) {
        try{
            return true ; // 추후 개발
        }catch(Exception e){
            log.error("Failed to inactive member: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public ActiveMember findActiveMemberById(Long memberId) {
        try{
            return em.find(ActiveMember.class, memberId);
        }catch (Exception e){
            log.error("Failed to find active member: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isNickNameDuplicatedWith(String nickname, Long eventId) {
        try{
            return em.createQuery("select count(am) from ActiveMember am " +
                            "where am.nickname = :nickname " +
                            "and am.event.id = :eventId", Long.class)
                    .setParameter("nickname", nickname)
                    .setParameter("eventId", eventId)
                    .getSingleResult() > 0;
        }catch (Exception e){
            log.error("Failed to check nickname duplication: {}", e.getMessage());
            return false;
        }
    }

    public void add(ActiveMember activeMember) {
        try{
            em.persist(activeMember);
        }catch (Exception e){
            log.error("Failed to check nickname duplication: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
