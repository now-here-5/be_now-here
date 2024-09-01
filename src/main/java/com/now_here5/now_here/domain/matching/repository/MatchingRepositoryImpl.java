package com.now_here5.now_here.domain.matching.repository;

import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.Status;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MatchingRepositoryImpl implements MatchingRepository {

    private final EntityManager em;

    @Override
    public List<Object[]> findMemberForBanner(Status status) {
        try {
            return em.createQuery("SELECT s.nickname, s.mbti, r.nickname, r.mbti " +
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
}
