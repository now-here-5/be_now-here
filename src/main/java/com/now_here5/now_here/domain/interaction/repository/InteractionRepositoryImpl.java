package com.now_here5.now_here.domain.interaction.repository;

import com.now_here5.now_here.domain.interaction.entity.Feedback;
import com.now_here5.now_here.domain.interaction.entity.Inquiry;
import com.now_here5.now_here.domain.interaction.entity.WithdrawalReason;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class InteractionRepositoryImpl implements InteractionRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public void saveFeedback(Feedback feedback) {
        try {
            em.persist(feedback);
        } catch (Exception e) {
            log.error("피드백 저장에 실패했습니다: {}", e.getMessage());
            throw new RuntimeException("피드백 저장에 실패했습니다", e);
        }
    }

    @Override
    public void saveInquiry(Inquiry inquiry) {
        try {
            em.persist(inquiry);
        } catch (Exception e) {
            log.error("문의 저장에 실패했습니다: {}", e.getMessage());
            throw new RuntimeException("문의 저장에 실패했습니다", e);
        }
    }

    @Override
    public void saveWithdrawalReason(WithdrawalReason withdrawalReason) {
        try {
            em.persist(withdrawalReason);
        } catch (Exception e) {
            log.error("탈퇴 사유 저장에 실패했습니다: {}", e.getMessage());
            throw new RuntimeException("탈퇴 사유 저장에 실패했습니다", e);
        }
    }

    @Override
    public boolean isFeedbackFullyWrittenToday(Long memberId) {
        try{
            return em.createQuery("select count(f) from Feedback f " +
                            "where f.member.id = :memberId " +
                            "and f.field is not null " +
                            "and f.createdAt >= current_date", Long.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult() >= 1;
        } catch (Exception e) {
            log.error("오늘 FULL 피드백 작성 여부 조회에 실패했습니다: {}", e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Feedback findFeedbackById(Long id) {
        try {
            return em.find(Feedback.class, id);
        } catch (Exception e) {
            log.error("ID로 피드백 조회에 실패했습니다: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Inquiry findInquiryById(Long id) {
        try {
            return em.find(Inquiry.class, id);
        } catch (Exception e) {
            log.error("ID로 문의 조회에 실패했습니다: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public WithdrawalReason findWithdrawalReasonById(Long id) {
        try {
            return em.find(WithdrawalReason.class, id);
        } catch (Exception e) {
            log.error("ID로 탈퇴 사유 조회에 실패했습니다: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Feedback> findAllFeedback() {
        try {
            return em.createQuery("select f from Feedback f", Feedback.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("모든 피드백 조회에 실패했습니다: {}", e.getMessage());
            throw new RuntimeException("모든 피드백 조회에 실패했습니다", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Inquiry> findAllInquiries() {
        try {
            return em.createQuery("select i from Inquiry i", Inquiry.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("모든 문의 조회에 실패했습니다: {}", e.getMessage());
            throw new RuntimeException("모든 문의 조회에 실패했습니다", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<WithdrawalReason> findAllWithdrawalReasons() {
        try {
            return em.createQuery("select w from WithdrawalReason w", WithdrawalReason.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("모든 탈퇴 사유 조회에 실패했습니다: {}", e.getMessage());
            throw new RuntimeException("모든 탈퇴 사유 조회에 실패했습니다", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Feedback> findFeedbacksByMemberId(Long memberId) {
        try {
            return em.createQuery("select f from Feedback f where f.member.id = :memberId", Feedback.class)
                    .setParameter("memberId", memberId)
                    .getResultList();
        } catch (Exception e) {
            log.error("멤버 ID로 피드백 조회 실패: {}", e.getMessage());
            throw new RuntimeException("멤버 ID로 피드백 조회 실패", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Inquiry> findInquiriesByMemberId(Long memberId) {
        try {
            return em.createQuery("select i from Inquiry i where i.member.id = :memberId", Inquiry.class)
                    .setParameter("memberId", memberId)
                    .getResultList();
        } catch (Exception e) {
            log.error("멤버 ID로 문의 조회 실패: {}", e.getMessage());
            throw new RuntimeException("멤버 ID로 문의 조회 실패", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<WithdrawalReason> findWithdrawalReasonsByMemberId(Long memberId) {
        try {
            return em.createQuery("select w from WithdrawalReason w where w.member.id = :memberId", WithdrawalReason.class)
                    .setParameter("memberId", memberId)
                    .getResultList();
        } catch (Exception e) {
            log.error("멤버 ID로 탈퇴 사유 조회 실패: {}", e.getMessage());
            throw new RuntimeException("멤버 ID로 탈퇴 사유 조회 실패", e);
        }
    }
}
