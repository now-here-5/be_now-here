package com.now_here5.now_here.domain.matching.repository;

import com.now_here5.now_here.domain.matching.dto.MatchingWithNicknameResponse;
import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.Status;
import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MatchingRepositoryImpl implements MatchingRepository {

    private final EntityManager em;

    @Override
    public List<Object[]> findMemberForBanner(Status status) {
        try {
            return em.createQuery("SELECT s.nickname, s.mbti, s.gender, r.nickname, r.mbti, s.gender " +
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
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public void save(Matching matching) {
        try {
            em.persist(matching);
        } catch (Exception e) {
            log.error("매칭 저장 중에 실패: {}", matching, e);
            throw new RuntimeException(e.getMessage());
        }
    }

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
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(Long matchingId) {
        try {
            Matching matching = em.find(Matching.class, matchingId);
            if (matching != null) {
                em.remove(matching);
            } else {
                log.warn("ID로 매칭 못 찾음: {}", matchingId);
            }
        } catch (Exception e) {
            log.error("ID로 매칭 삭제 중 실패: {}", matchingId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Long countByReceiverId(Long memberId) {
        try {
            return em.createQuery("SELECT COUNT(m) FROM Matching m WHERE m.receiver.id = :memberId", Long.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("Failed to count matchings by receiverId: {}", memberId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Long countBySenderId(Long memberId) {
        try {
            return em.createQuery("SELECT COUNT(m) FROM Matching m WHERE m.sender.id = :memberId", Long.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("Failed to count matchings by senderId: {}", memberId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Matching findBySenderAndReceiver(Member sender, Member receiver) {
        try {
            return em.createQuery("SELECT m FROM Matching m WHERE m.sender = :sender AND m.receiver = :receiver", Matching.class)
                    .setParameter("sender", sender)
                    .setParameter("receiver", receiver)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("보낸이와 받는이로 매칭 조회 중 실패: {} {}", sender, receiver, e);
            return null;
        }
    }

    @Override
    public boolean existsByMembers(Member member1, Member member2) {
        try {
            em.createQuery("SELECT m FROM Matching m WHERE (m.sender = :member1 AND m.receiver = :member2) OR (m.sender = :member2 AND m.receiver = :member1)", Matching.class)
                    .setParameter("member1", member1)
                    .setParameter("member2", member2)
                    .getSingleResult();
            return true;
        } catch (Exception e) {
            log.error("두 멤버로 매칭 조회 중 실패: {} {}", member1, member2, e);
            return false;
        }
}

    @Override
    public List<Matching> findByReceiverId(Long memberId) {
        try {
            return em.createQuery("SELECT m FROM Matching m WHERE m.receiver.id = :memberId", Matching.class)
                    .setParameter("memberId", memberId)
                    .getResultList();
        } catch (Exception e) {
            log.error("받는 이로 매칭 조회 중 실패: {}", memberId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Matching> findBySenderId(Long memberId) {
        try {
            return em.createQuery("SELECT m FROM Matching m WHERE m.sender.id = :memberId", Matching.class)
                    .setParameter("memberId", memberId)
                    .getResultList();
        } catch (Exception e) {
            log.error("보낸 이로 매칭 조회 중 실패: {}", memberId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<MatchingWithNicknameResponse> findMatchingWithNickname(Long memberId) {
        try {
            return em.createQuery("SELECT new com.now_here5.now_here.domain.matching.dto.MatchingWithNicknameResponse(" +
                            "m, " +
                            "CASE " +
                            "WHEN m.sender.id = :memberId THEN r.nickname " +
                            "WHEN m.receiver.id = :memberId THEN s.nickname " +
                            "END" +
                            ") " +
                            "FROM Matching m " +
                            "JOIN FETCH m.sender s " +
                            "JOIN FETCH m.receiver r " +
                            "WHERE (m.sender.id = :memberId AND m.status <> :pendingStatus) " +
                            "   OR (m.receiver.id = :memberId AND m.status <> :rejectedStatus) " +
                            "ORDER BY m.createdAt DESC", MatchingWithNicknameResponse.class)
                    .setParameter("memberId", memberId)
                    .setParameter("pendingStatus", Status.PENDING)
                    .setParameter("rejectedStatus", Status.REJECTED)
                    .getResultList();
        } catch (Exception e) {
            log.error("Failed to find matchings with counterpart nickname: memberId={}, error={}", memberId, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }



    @Override
    public List<Matching> findAcceptedMatchingsBySenderOrReceiver(Long memberId) {
        try {
            return em.createQuery("SELECT m FROM Matching m " +
                            "WHERE m.status = :status AND " +
                            "(m.sender.id = :memberId OR m.receiver.id = :memberId)", Matching.class)
                    .setParameter("status", Status.ACCEPTED)
                    .setParameter("memberId", memberId)
                    .getResultList();
        } catch (Exception e) {
            log.error("매칭 조회 중 실패: memberId={}, status=ACCEPTED, error={}", memberId, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

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
            throw new RuntimeException(e.getMessage());
        }
    }
}
