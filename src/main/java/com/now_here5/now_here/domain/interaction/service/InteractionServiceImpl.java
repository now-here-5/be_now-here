package com.now_here5.now_here.domain.interaction.service;

import com.now_here5.now_here.domain.interaction.dto.FeedbackRequect;
import com.now_here5.now_here.domain.interaction.dto.InquiryRequest;
import com.now_here5.now_here.domain.interaction.dto.WithdrawalReasonRequest;
import com.now_here5.now_here.domain.interaction.entity.Feedback;
import com.now_here5.now_here.domain.interaction.entity.Inquiry;
import com.now_here5.now_here.domain.interaction.entity.WithdrawalReason;
import com.now_here5.now_here.domain.interaction.repository.InteractionRepository;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {

    private final InteractionRepository interactionRepository;
    private final MemberRepository memberRepository;
    private final AuthUtil authUtil;

    @Override
    public void createFeedback(FeedbackRequect feedbackRequect) {
        Long memberId = authUtil.getMemberByAuthentication().getMemberId();
        Member member = memberRepository.findActiveMemberById(memberId);
        Feedback feedback = Feedback.builder()
                .content(feedbackRequect.getContent())
                .member(member)
                .field(feedbackRequect.getField())
                .build();
        interactionRepository.saveFeedback(feedback);
    }

    @Override
    public void createInquiry(InquiryRequest inquiryRequest) {
        Long memberId = authUtil.getMemberByAuthentication().getMemberId();
        Member member = memberRepository.findActiveMemberById(memberId);
        Inquiry inquiry = Inquiry.builder()
                .content(inquiryRequest.getContent())
                .member(member)
                .answered(inquiryRequest.isAnswered())
                .build();
        interactionRepository.saveInquiry(inquiry);
    }

    @Override
    public void createWithdrawalReason(WithdrawalReasonRequest withdrawalReasonRequest) {
        Long memberId = authUtil.getMemberByAuthentication().getMemberId();
        Member member = memberRepository.findActiveMemberById(memberId);
        WithdrawalReason withdrawalReason = WithdrawalReason.builder()
                .member(member)
                .content(withdrawalReasonRequest.getContent())
                .build();
        interactionRepository.saveWithdrawalReason(withdrawalReason);
    }

    @Override
    public List<Feedback> getFeedbacksByMemberId(Long memberId) {
        return interactionRepository.findFeedbacksByMemberId(memberId);
    }

    @Override
    public List<Inquiry> getInquiriesByMemberId(Long memberId) {
        return interactionRepository.findInquiriesByMemberId(memberId);
    }

    @Override
    public List<WithdrawalReason> getWithdrawalReasonsByMemberId(Long memberId) {
        return interactionRepository.findWithdrawalReasonsByMemberId(memberId);
    }
}
