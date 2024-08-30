package com.now_here5.now_here.domain.interaction.service;

import com.now_here5.now_here.domain.interaction.dto.FeedbackRequect;
import com.now_here5.now_here.domain.interaction.dto.InquiryRequest;
import com.now_here5.now_here.domain.interaction.dto.InquiryResponse;
import com.now_here5.now_here.domain.interaction.dto.WithdrawalReasonRequest;
import com.now_here5.now_here.domain.interaction.entity.Feedback;
import com.now_here5.now_here.domain.interaction.entity.Inquiry;
import com.now_here5.now_here.domain.interaction.entity.WithdrawalReason;
import com.now_here5.now_here.domain.interaction.repository.InteractionRepository;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.util.AuthUtil;
import com.now_here5.now_here.infra.phone.service.PhoneService;
import com.now_here5.now_here.infra.slack.service.SlackInquiryHandlerService;
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
    private final SlackInquiryHandlerService slackInquiryHandlerService;
    private final PhoneService phoneService;

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
        slackInquiryHandlerService.sendSlackNotification(inquiry.getId(), inquiry.getContent(), "01022223333");
    }

    @Override
    public void processInquiryResponse(Long inquiryId, String answer) {
        Inquiry foundInquiry = interactionRepository.findInquiryById(inquiryId);

        if (foundInquiry!=null) {
           // foundInquiry.setAnswered(true); // 답변 완료 처리 필드 대신 답변 내용이 채워져 있는지로 구분 가능할듯.
           //  foundInquiry.setAnswer(answer); 나중에는 이것만 사용.

            String responseMessage = String.format("[Now, Here] 질문에 대한 답변: %s", answer);
            InquiryResponse inquiryResponse = InquiryResponse.builder()
                    .inquiryId(inquiryId)
                    .answer(responseMessage)
                    .inquiryContent(foundInquiry.getContent())
                    .build();

            // SMS 전송
//            phoneService.sendSms(foundInquiry.getPhoneNubmer(), responseMessage); 나중에 이거 이용.
              phoneService.sendSms("01022223333", inquiryResponse);
        } else {
            System.out.println("Inquiry not found for ID: " + inquiryId);
        }
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
