package com.now_here5.now_here.domain.interaction.repository;

import com.now_here5.now_here.domain.interaction.entity.Feedback;
import com.now_here5.now_here.domain.interaction.entity.Inquiry;
import com.now_here5.now_here.domain.interaction.entity.WithdrawalReason;

import java.util.List;

public interface InteractionRepository {

    void saveFeedback(Feedback feedback);

    void saveInquiry(Inquiry inquiry);

    void saveWithdrawalReason(WithdrawalReason withdrawalReason);

    boolean isFeedbackFullyWrittenToday(Long memberId);

    boolean isFeedbackFirstWritten(Long memberId);

    Feedback findFeedbackById(Long id);

    Inquiry findInquiryById(Long id);

    WithdrawalReason findWithdrawalReasonById(Long id);

    List<Feedback> findAllFeedback();

    List<Inquiry> findAllInquiries();

    List<WithdrawalReason> findAllWithdrawalReasons();

    List<Feedback> findFeedbacksByMemberId(Long memberId);

    List<Inquiry> findInquiriesByMemberId(Long memberId);

    List<WithdrawalReason> findWithdrawalReasonsByMemberId(Long memberId);
}

