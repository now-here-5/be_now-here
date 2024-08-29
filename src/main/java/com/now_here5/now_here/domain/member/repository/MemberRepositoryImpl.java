package com.now_here5.now_here.domain.member.repository;

import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
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
    public List<Member> findActiveMemberByPhone(String phoneNumber) {
        try{
            return em.createQuery("select m from Member m " +
                            "join fetch m.event " +
                            "where m.phoneNumber = :phoneNumber " +
                            "and m.active = true", Member.class)
                    .setParameter("phoneNumber", phoneNumber)
                    .getResultList();
        }catch (Exception e){
            log.error("Failed to find active member by phone number: {}", phoneNumber);
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
    public boolean isNickNameDuplicatedWith(String nickname, Long eventId) {
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

    public void save(Member activeMember) {
        try{
            em.persist(activeMember);
        }catch (Exception e){
            log.error("Failed to check nickname duplication: {}", e.getMessage());
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
    public List<Member> findMembersByEventIdAndGender(Long eventId, Gender mygender) {
        try {
            return em.createQuery("select am from Member am " +
                            "join fetch am.event " +
                            "where am.event.id = :eventId " +
                            "and am.gender != :mygender " +
                            "order by random()", Member.class)
                    .setParameter("eventId", eventId)
                    .setParameter("mygender", mygender)
                    .setMaxResults(2)
                    .getResultList();
        } catch (NoResultException e) {
            log.warn("해당하는 멤버가 없습니다 이벤트: {} 성별: {}", eventId, mygender);
            return List.of();
        } catch (Exception e) {
            log.error("멤버를 찾는데 실패했습니다: {}", e.getMessage());
            throw new RuntimeException("멤버를 찾는데 실패했습니다", e);
        }
    }
}
