package com.now_here5.now_here.domain.interaction.service;

import com.now_here5.now_here.domain.interaction.dto.FeedbackRequect;
import com.now_here5.now_here.domain.interaction.dto.InquiryRequest;
import com.now_here5.now_here.domain.interaction.dto.WithdrawalReasonRequest;
import com.now_here5.now_here.domain.interaction.entity.Feedback;
import com.now_here5.now_here.domain.interaction.entity.Inquiry;
import com.now_here5.now_here.domain.interaction.entity.WithdrawalReason;
import com.now_here5.now_here.domain.interaction.repository.InteractionRepository;
import com.now_here5.now_here.domain.matching.repository.MatchingRepository;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.domain.member.service.MemberService;
import com.now_here5.now_here.global.util.AuthUtil;
import com.now_here5.now_here.infra.email.service.EmailInquiryService;
import com.now_here5.now_here.infra.slack.service.SlackInquiryHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionServiceImpl implements InteractionService {

    private final InteractionRepository interactionRepository;
    private final MemberRepository memberRepository;
    private final AuthUtil authUtil;
    private final SlackInquiryHandlerService slackInquiryHandlerService;
    private final EmailInquiryService emailInquiryService;
    private final MatchingRepository matchingRepository;
    private final MemberService memberservice;


    @Transactional
    @Override
    public void createFeedback(FeedbackRequect feedbackRequect) {
        try{
            Long memberId = authUtil.getMemberByAuthentication().getMemberId();
            Member member = memberRepository.findActiveMemberById(memberId);
            Feedback feedback = Feedback.builder()
                    .content(feedbackRequect.getContent())
                    .member(member)
                    .field(feedbackRequect.getField())
                    .build();
            interactionRepository.saveFeedback(feedback);
            memberservice.offerSpecialHeartIfQualified(authUtil.getMemberByAuthentication().getMemberId(),member.getSpecialHeart()+5 ); // 비동기적으로 처리
        }catch (Exception e) {
            log.error("토큰이 없는 피드백 작성: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }



    @Transactional
    @Override
    public void createInquiry(InquiryRequest inquiryRequest) {
        Inquiry.InquiryBuilder builder = Inquiry.builder();
        try {
            Long memberId = authUtil.getMemberByAuthentication().getMemberId();
            builder.member(memberRepository.findActiveMemberById(memberId));
        }catch(Exception e){
            log.error("토큰이 없는 문의사항 작성: {}", e.getMessage());
        }finally {
            builder.content(inquiryRequest.getContent())
                    .email(inquiryRequest.getEmail());
        }

        Inquiry newInquiry = builder.build();
        interactionRepository.saveInquiry(newInquiry);
        slackInquiryHandlerService.sendSlackNotification(newInquiry.getId(), newInquiry.getContent(), newInquiry.getEmail());
    }

    @Transactional
    @Override
    public void processInquiryResponse(Long inquiryId, String answer) {
        Inquiry foundInquiry = interactionRepository.findInquiryById(inquiryId);

        if (foundInquiry!=null) {

            foundInquiry.updateAnswer(answer);

            // SMS 전송
            emailInquiryService.setUpAndSendEmail(foundInquiry.getEmail(), foundInquiry.getContent(), foundInquiry.getAnswer());
        } else {
            log.info("Inquiry not found for ID: {}", inquiryId);
        }
    }


/* (의견남기기 요구) 팝업이 뜨는 경우
1. 계정 기준: 최초 한번, 매칭 후 매칭 현황 페이지 접속했을 때 (매칭이 되었을 때만)
2. 하루 기준: 오늘 매칭 페이지에 "3번째" 접속했을 때 팝업이 뜨도록 설정 (첫 번째, 두 번째, 4번째 이후에는 팝업 없음)
3. 하루 기준: 오늘 한 번이라도 의견을 full로 작성하지 않은 사용자 (의견과 별점을 모두 남긴 사람은 패스, 의견만 남긴 사용자에게는 위의 1번 또는 2번 조건 적용)
*/

/* 로직 설명
1. 최초 한번 매칭 후 매칭 현황 페이지 접속 시:
    - 매칭이 되었는지 확인 (매칭 테이블에서 현재 사용자 ID로 매칭된 accepted 상태가 있으면 true, 없으면 false 반환).

2. 하루 기준 오늘 매칭 페이지 3번째 접속 시:
    - getFeedbackStatus() 메서드가 호출될 때마다 popUpCount를 업데이트하고, 현재 카운트를 바탕으로 팝업 표시 여부 결정.
    - popUpCount가 4일 때 true를 반환하여 팝업 표시, 그 외에는 false 반환.

3. 하루 기준 오늘 의견을 full로 작성하지 않은 경우:
    - 오늘 작성된 피드백(의견과 별점 모두)이 있는지 확인하여, 있으면 false, 없으면 true 반환.
*/

/* 확인 순서:
1. Step 1: 오늘 작성된 피드백(의견과 별점 모두) 확인 [(별점 && 내용) != null]. 작성된 피드백이 있으면 return false하고 2번, 3번 쿼리를 생략.
2. Step 2: 매칭 상태 확인. 매칭이 없을 경우 불필요한 쿼리 발생 가능성 있음. 따라서 먼저 피드백을 확인한 후, 매칭 여부 확인.
3. Step 3: 매칭이 확인된 경우 popUpCount를 확인하여, popUpCount가 4이면 true를 반환하고 팝업을 띄움. 그렇지 않으면 false 반환.
4. Step 4: popUpCount가 4가 아니면 매칭 여부 확인 후, 매칭이 되지 않았으면 true, 되었으면 false 반환.

// 전체 흐름 정리:
1. 사용자가 매칭 페이지로 이동하는 버튼을 클릭할 때, GET 요청을 보냄 (base_url/interaction/feedback/status).
2. 서버에서 팝업을 띄울지 여부를 판단.
3. 결과로 boolean 값을 반환 (true: 팝업 표시, false: 팝업 미표시).
4. 하루에 한 번, 스케줄러가 실행되어 모든 사용자의 팝업 카운트를 초기화
    - 음수인 경우 0으로 초기화
    - 양수인 경우 1로 초기화
*/


    @Transactional
    @Override
    public boolean getFeedbackStatus() {

        try{
            Long memberId = authUtil.getMemberByAuthentication().getMemberId();

            // Step 1: 오늘 피드백을 작성했는지 확인
            if (isFeedbackFullyWrittenToday(memberId)) {
                return false;
            }

            Member member = memberRepository.findMemberById(memberId);
            if (member == null) {
                log.warn("Member not found for ID: {}", memberId);
                throw new RuntimeException("Member not found for ID: " + memberId);
            }

            int currentCount = member.getPopupCount();

            // Step 2: 매칭이 되었는지 확인
            boolean matched = isMatched(memberId);

            // Step 3: 팝업 로직
            if (currentCount == 0) { // 첫 접근
                if (matched) {
                    member.updatePopupCount(2); // 최초 팝업 후 카운트 2로 설정
                    return true; // 팝업 띄우기
                } else {
                    member.updatePopupCount(-2); // 매칭이 안 되었으므로 음수로 카운트
                    return false;
                }
            } else if (isPopupRequired(currentCount)) { // 이미 카운트가 4인 경우: 팝업 이미 뜬 상태
                return false; // update 쿼리 생략하고, 팝업 띄우지 않음
            } else if (currentCount > 0) { // 팝업이 이미 한번 뜬 경우
                int updatedCount = member.updatePopupCount(currentCount + 1);
                return isPopupRequired(updatedCount); // 3번째 접근 시 팝업 띄우기
            } else { // 최초 매칭이 안 되어 음수 카운트 중인 경우
                int updatedCount = member.updatePopupCount(currentCount - 1);
                return isPopupRequired(updatedCount); // 3번째 접근 시 팝업 띄우기
            }
        }catch (Exception e){
            log.error("피드백 팝업 상태 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean isMatched(Long memberId) {
        return !matchingRepository.findAcceptedMatchingsBySenderOrReceiver(memberId).isEmpty();
    }

    private boolean isFeedbackFullyWrittenToday(Long memberId) {
        return interactionRepository.isFeedbackFullyWrittenToday(memberId);
    }

    private boolean isPopupRequired(int count) {
        return Math.abs(count) == 4;
    }


    @Transactional
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

    @Transactional(readOnly = true)
    @Override
    public List<Feedback> getFeedbacksByMemberId(Long memberId) {
        return interactionRepository.findFeedbacksByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Inquiry> getInquiriesByMemberId(Long memberId) {
        return interactionRepository.findInquiriesByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<WithdrawalReason> getWithdrawalReasonsByMemberId(Long memberId) {
        return interactionRepository.findWithdrawalReasonsByMemberId(memberId);
    }
}
