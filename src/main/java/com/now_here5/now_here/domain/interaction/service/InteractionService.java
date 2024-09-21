package com.now_here5.now_here.domain.interaction.service;

import com.now_here5.now_here.domain.interaction.dto.FeedbackRequect;
import com.now_here5.now_here.domain.interaction.dto.InquiryRequest;
import com.now_here5.now_here.domain.interaction.dto.WithdrawalReasonRequest;
import com.now_here5.now_here.domain.interaction.entity.Feedback;
import com.now_here5.now_here.domain.interaction.entity.Inquiry;
import com.now_here5.now_here.domain.interaction.entity.WithdrawalReason;

import java.util.List;

public interface InteractionService {

    void createFeedback(FeedbackRequect feedback);

    void createInquiry(InquiryRequest inquiry);

    void createWithdrawalReason(WithdrawalReasonRequest withdrawalReason);

    List<Feedback> getFeedbacksByMemberId(Long memberId);

    List<Inquiry> getInquiriesByMemberId(Long memberId);

    List<WithdrawalReason> getWithdrawalReasonsByMemberId(Long memberId);

    void processInquiryResponse(Long inquiryId, String answer);

    boolean getFeedbackStatus();


}
