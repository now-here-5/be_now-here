package com.now_here5.now_here.domain.member.repository;

import com.now_here5.now_here.domain.member.entity.Gender;
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
    public List<Member> findActiveMemberByPhoneNumber(String phoneNumber) {
        try{
            return em.createQuery("select m from Member m " +
                            "join fetch m.event " +
                            "where m.phoneNumber = :phoneNumber " , Member.class)
                    .setParameter("phoneNumber", phoneNumber)
                    .getResultList();
        }catch (Exception e){
            log.error("Failed to find active member by phoneNumber: {}", phoneNumber);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean deactivateMember(Long memberId) {
       try {
           em.createQuery("UPDATE Member m " +
                           "SET m.active = false, m.token = null " +
                           "WHERE m.id = :memberId")
                   .setParameter("memberId", memberId)
                   .executeUpdate();
           em.flush();

           return true;
       }catch (Exception e){
           log.error("Failed to inactive member: {}", e.getMessage());
           return false;
       }
    }

    @Override
    public void deactivateBulkMembersByEventId(Long eventId) {
        try{
            em.createQuery("update Member m " +
                            "set m.active = false, m.token = null " +
                            "where m.event.id = :eventId")
                    .setParameter("eventId", eventId)
                    .executeUpdate();
        }catch (Exception e){
            log.error("Failed to inactive members by event id: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public Member findActiveMemberById(Long memberId) {
        try{
            return em.createQuery("select am from Member am " +
                            "join fetch am.event " +
                            "where am.id = :memberId " +
                            "and am.active = true", Member.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        }catch (Exception e){
            log.error("Failed to find active member: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isNickNameDuplicatedInEvent(String nickname, Long eventId) {
        try{
            return em.createQuery("select count(am) from Member am " +
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

    @Override
    public boolean isPhoneDuplicated(String phoneNumber, Long eventId) {
        try{
            return em.createQuery("select count(am) from Member am " +
                            "where am.phoneNumber = :phoneNumber " +
                            "and am.event.id = :eventId", Long.class)
                    .setParameter("phoneNumber", phoneNumber)
                    .setParameter("eventId", eventId)
                    .getSingleResult() > 0;
        }catch (Exception e){
            log.error("Failed to check phone number duplication: {}", e.getMessage());
            return false;
        }
    }

    public void save(Member activeMember) {
        try{
            em.persist(activeMember);
        }catch (Exception e){
            log.error("Failed to check nickname duplication: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public void initializePopupValue() {
        try {
            em.createQuery("UPDATE Member SET popupCount = " +
                            "CASE " +
                            "WHEN popupCount <= 0 " +
                            "THEN 0 ELSE 1 " +
                            "END")
            .executeUpdate();
        } catch (Exception e) {
            log.error("Failed to initialize popup value: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public Member findMemberById(Long memberId) {
        try{
            return em.find(Member.class, memberId);
        }catch (Exception e){
            log.error("Failed to find member by id: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Member> findMembersByMemberIdAndEventIdAndGender(Long memberId, Long eventId, Gender gender) {
        try {
            return em.createQuery("SELECT m FROM Member m " +
                            "WHERE m.event.id = :eventId " +
                            "AND m.gender = :gender " +
                            "AND m.id != :memberId " +
                            "AND NOT EXISTS (" +
                            "   SELECT 1 FROM Matching match " +
                            "   WHERE (match.senderMemberId = :memberId AND match.receiverMemberId = m.id) " +
                            "   OR (match.receiverMemberId = :memberId AND match.senderMemberId = m.id) " +
                            ") " +
                            "AND m.active = true " +
                            "ORDER BY random()", Member.class)  // 무작위 정렬
                    .setParameter("memberId", memberId)
                    .setParameter("eventId", eventId)
                    .setParameter("gender", gender)
                    .setMaxResults(10)  // 최대 10명 선택
                    .getResultList();
        } catch (Exception e) {
            log.error("Failed to find members by memberId, eventId and gender: {}", e.getMessage());
            throw new RuntimeException("Failed to find members", e);
        }
    }
}
