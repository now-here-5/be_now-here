package com.now_here5.now_here.domain.matching.repository;

import com.now_here5.now_here.domain.matching.dto.MatchingWithNicknameResponse;
import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.Status;
import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MatchingRepositoryImpl implements MatchingRepository {

    private final EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> findMemberForBanner(Status status) {
        try {
            return em.createQuery("SELECT s.nickname, s.mbti, s.gender, r.nickname, r.mbti, r.gender " +
                            "FROM Matching m " +
                            "JOIN m.sender s " +
                            "JOIN m.receiver r " +
                            "WHERE m.status = :status " +
                            "ORDER BY m.createdAt DESC", Object[].class)
                    .setParameter("status", status)
                    .setMaxResults(10)
                    .getResultList();
        } catch (Exception e) {
            log.error("배너에 적힐 10명의 매칭을 찾는 도중에 에러가 발생했습니다: {}", status, e);
            return List.of(); // 빈 리스트 반환
        }
    }

    @Override
    public void save(Matching matching) {
        try {
            em.persist(matching);
        } catch (Exception e) {
            log.error("매칭 저장 중에 실패: {}", matching, e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Matching findById(Long matchingId) {
        try {
            return em.find(Matching.class, matchingId);
        } catch (Exception e) {
            log.error("ID로 매칭 조회중 실패: {}", matchingId, e);
            return null;
        }
    }

    @Override
    public void update(Matching matching) {
        try {
            em.merge(matching);
        } catch (Exception e) {
            log.error("매칭 업데이트 중 실패: {}", matching, e);
        }
    }

    @Override
    public void removeSentHeart(Long senderId, Long receiverId) {
        try{
            em.createQuery("delete from Matching m where " +
                            "m.sender.id =:senderId and " +
                            "m.receiver.id =:receiverId")
                    .setParameter("senderId", senderId)
                    .setParameter("receiverId", receiverId)
                    .executeUpdate();
        }catch(Exception e) {
            log.error("보낸 하트 취소 실패 : {}" , e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Long countByReceiverId(Long memberId) {
        try {
            return em.createQuery("SELECT COUNT(m) FROM Matching m " +
                            " WHERE m.receiver.id = :memberId and " +
                            "m.status = : status and " +
                            "m.sender.active = true ", Long.class)
                    .setParameter("memberId", memberId)
                    .setParameter("status", Status.PENDING)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("Failed to count matchings by receiverId: {}", memberId, e);
            return 0L; // 0을 반환하여 처리
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Long countBySenderId(Long memberId) {
        try {
            return em.createQuery("SELECT COUNT(m) FROM Matching m " +
                            " WHERE m.sender.id = :memberId and " +
                            "m.status = : status and " +
                            "m.receiver.active = true", Long.class)
                    .setParameter("memberId", memberId)
                    .setParameter("status", Status.PENDING)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("Failed to count matchings by senderId: {}", memberId, e);
            return 0L; // 0을 반환하여 처리
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Matching findBySenderAndReceiver(Long senderId, Long receiverId) {
        try {
            return em.createQuery("SELECT m FROM Matching m " +
                            "WHERE m.sender.id = :senderId AND m.receiver.id = :receiverId", Matching.class)
                    .setParameter("senderId", senderId)
                    .setParameter("receiverId", receiverId)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.warn("No matching found between sender {} and receiver {}.", senderId, receiverId);
            return null; // 결과가 없을 때 null 반환
        } catch (Exception e) {
            log.error("보낸이와 받는이로 매칭 조회 중 에러 발생", e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isExistsByMemberIds(Long id1, Long id2) {
        try {
            return em.createQuery("SELECT count(m) FROM Matching m " +
                            "WHERE (m.sender.id = :id1 AND m.receiver.id = :id2) " +
                            "   OR (m.sender.id = :id2 AND m.receiver.id = :id1)", Long.class)
                    .setParameter("id1", id1)
                    .setParameter("id2", id2)
                    .getSingleResult() > 0;
        } catch (NoResultException e) {
            log.warn("No matching found between {} and {}", id1, id2);
            return false;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Matching> findByReceiverId(Long memberId) {
        try {
            return em.createQuery("SELECT m FROM Matching m " +
                            "JOIN FETCH m.receiver r " +
                            "JOIN FETCH m.sender s " +
                            "WHERE r.id = :memberId AND " +
                            "m.status = :status AND " +
                            "s.active = true", Matching.class) // 계정이 활성화된 유저만 조회해야 됨.
                    .setParameter("memberId", memberId)
                    .setParameter("status", Status.PENDING)
                    .getResultList();
        } catch (Exception e) {
            log.error("받는 이로 매칭 조회 중 실패: {}", memberId, e);
            return List.of(); // 빈 리스트 반환
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Matching> findBySenderId(Long memberId) {
        try {
            return em.createQuery("SELECT m FROM Matching m " +
                            "JOIN FETCH m.receiver r " +
                            "JOIN FETCH m.sender s " +
                            "WHERE s.id = :memberId and " +
                            "m.status = :status and " +
                            "r.active = true", Matching.class) // 계정이 활성화된 유저만 조회해야 됨.
                    .setParameter("memberId", memberId)
                    .setParameter("status", Status.PENDING)
                    .getResultList();
        } catch (Exception e) {
            log.error("보낸 이로 매칭 조회 중 실패: {}", memberId, e);
            return List.of(); // 빈 리스트 반환
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<MatchingWithNicknameResponse> findMatchingWithNickname(Long memberId) {
        try {
            return em.createQuery("SELECT new com.now_here5.now_here.domain.matching.dto.MatchingWithNicknameResponse(" +
                            "m, " +
                            "CASE " +
                            "WHEN s.id = :memberId THEN r.nickname " +
                            "WHEN r.id = :memberId THEN s.nickname " +
                            "END" +
                            ") " +
                            "FROM Matching m " +
                            "JOIN FETCH m.sender s " +
                            "JOIN FETCH m.receiver r " +
                            "WHERE (s.id = :memberId AND m.status <> :pendingStatus AND r.active = true) " +
                            "   OR (r.id = :memberId AND m.status <> :rejectedStatus AND s.active = true) " +
                            "ORDER BY m.createdAt DESC", MatchingWithNicknameResponse.class)
                    .setParameter("memberId", memberId)
                    .setParameter("pendingStatus", Status.PENDING)
                    .setParameter("rejectedStatus", Status.REJECTED)
                    .setMaxResults(30) // 최대 최근 25개로 제한 (성능 문제)
                    .getResultList();
        } catch (Exception e) {
            log.error("Failed to find matchings with counterpart nickname: memberId={}, error={}", memberId, e.getMessage());
            return List.of(); // 빈 리스트 반환
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Matching> findAcceptedMatchingsBySenderOrReceiver(Long memberId) {
        try {
            return em.createQuery("SELECT m FROM Matching m " +
                            "WHERE m.status = :status AND " +
                            "(m.sender.id = :memberId OR m.receiver.id = :memberId) " +
                            "Order by m.modifiedAt desc ", Matching.class)
                    .setParameter("status", Status.ACCEPTED)
                    .setParameter("memberId", memberId)
                    .getResultList();
        } catch (Exception e) {
            log.error("매칭 조회 중 실패: memberId={}, status=ACCEPTED, error={}", memberId, e.getMessage());
            return List.of(); // 빈 리스트 반환
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Matching> findMatchingsFromYesterday() {
        try {
            LocalDateTime endOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            LocalDateTime startOfDay = endOfDay.minusDays(1);
            return em.createQuery("SELECT m FROM Matching m WHERE m.createdAt >= :startOfDay AND m.createdAt < :endOfDay", Matching.class)
                    .setParameter("startOfDay", startOfDay)
                    .setParameter("endOfDay", endOfDay)
                    .getResultList();
        } catch (Exception e) {
            log.error("어제의 매칭 조회중 실패: {}", e.getMessage());
            return List.of(); // 빈 리스트 반환
        }
    }
}
