package com.now_here5.now_here.domain.matching.service;

import com.now_here5.now_here.domain.matching.converter.MatchingListToDto;
import com.now_here5.now_here.domain.matching.dto.*;
import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.Status;
import com.now_here5.now_here.domain.matching.repository.MatchingRepository;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.util.AuthUtil;
import com.now_here5.now_here.infra.slack.service.SlackNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MatchingServiceImpl implements MatchingService {

    private final MatchingRepository matchingRepository;
    private final MatchingListToDto matchingListToDto;
    private final AuthUtil authUtil;
    private final MemberRepository memberRepository;
    private final SlackNotificationService slackNotificationService;

    @Cacheable("bannerListCache")// 메서드의 결과를 캐시하여 동일한 인자로 호출되면 캐시된 결과 반환
    @Override
    public List<BannerListResponse> getBannerList() {
        // 최적화된 쿼리로 데이터를 가져옴
        List<Object[]> results = matchingRepository.findMemberForBanner(Status.ACCEPTED);

        // 데이터를 DTO로 변환하여 반환
        return matchingListToDto.convertToBannerListResponse(results);
    }


    @Override
    public void sendLove(Long receiverId) {
        Long senderId = authUtil.getMemberByAuthentication().getMemberId();
        Member sender = memberRepository.findActiveMemberById(senderId);
        Member receiver = memberRepository.findActiveMemberById(receiverId);

        try {
            Matching matching = Matching.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .status(Status.PENDING)
                    .build();

            matchingRepository.save(matching);

            // receiver에게 받은 하트 알림을 전송
            String message = String.format("%s님이 %s님에게 하트를 보냈습니다.", sender.getNickname(), receiver.getNickname());
            slackNotificationService.sendNotification(message);
        } catch (Exception e) {
            log.error("Failed to send love from {} to {}: {}", senderId, receiverId, e.getMessage());
            throw new RuntimeException("Failed to send love", e);
        }
    }

    @Override
    public void receiveLove(Long senderId) {
        Long receiverId = authUtil.getMemberByAuthentication().getMemberId();
        Member sender = memberRepository.findActiveMemberById(senderId);
        Member receiver = memberRepository.findActiveMemberById(receiverId);

        try {
            Matching matching = matchingRepository.findBySenderAndReceiver(sender, receiver);
            matching.setStatus(Status.ACCEPTED);
            matchingRepository.update(matching);

            // receiver에게 매칭 알림 전송
            String rMessage = String.format("%s님과 매칭되었습니다.", sender.getNickname());
            slackNotificationService.sendNotification(rMessage);

            // sender에게 매칭 알림 전송
            String sMessage = String.format("%s님이 하트를 수락하였습니다.", receiver.getNickname());
            slackNotificationService.sendNotification(sMessage);

        } catch (Exception e) {
            log.error("Failed to receive love from {} to {}: {}", senderId, receiverId, e.getMessage());
            throw new RuntimeException("Failed to receive love", e);
        }
    }

    @Override
    public List<SummaryResponse> getSummary() {
        Long receiverId = authUtil.getMemberByAuthentication().getMemberId();
        Long receiveLove = matchingRepository.countByReceiverId(receiverId);
        Long sendLove = matchingRepository.countBySenderId(receiverId);

        return List.of(SummaryResponse.builder()
                .receiveLove(String.valueOf(receiveLove))
                .sendLove(String.valueOf(sendLove))
                .build());
    }

    @Override
    public List<SenderResponse> getSenderList() {
        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
        Long memberId = authMember.getMemberId();

        try {
            List<Matching> matchings = matchingRepository.findByReceiverId(memberId);
            return matchings.stream()
                    .map(m -> new SenderResponse(
                            m.getSender().getId(),
                            m.getSender().getMbti().toString(),
                            m.getSender().getBirthday().toString(),
                            m.getSender().getNickname(),
                            m.getSender().getGender().toString(),
                            m.getSender().getDescription()))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get sender list for member {}: {}", memberId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ReceiverResponse> getReceiverList() {
        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
        Long memberId = authMember.getMemberId();

        try {
            List<Matching> matchings = matchingRepository.findBySenderId(memberId);
            return matchings.stream()
                    .map(m -> new ReceiverResponse(
                            m.getReceiver().getId(),
                            m.getReceiver().getMbti().toString(),
                            m.getReceiver().getBirthday().toString(),
                            m.getReceiver().getNickname(),
                            m.getReceiver().getGender().toString(),
                            m.getReceiver().getDescription()))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get receiver list for member {}: {}", memberId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<SummaryDetailResponse> getAcceptedMatchings() {
        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
        Long memberId = authMember.getMemberId();

        try {
            List<Matching> matchings = matchingRepository.findAcceptedMatchingsBySenderOrReceiver(memberId);

            return matchings.stream()
                    .map(m -> {
                        if (m.getReceiver().getId().equals(memberId)) {
                            // 내가 receiver인 경우
                            return SummaryDetailResponse.builder()
                                    .memberId(m.getSender().getId().toString())
                                    .mbti(m.getSender().getMbti().toString())
                                    .birthdate(m.getSender().getBirthday().toString())
                                    .nickname(m.getSender().getNickname())
                                    .gender(m.getSender().getGender().toString())
                                    .description(m.getSender().getDescription())
                                    .phoneNumber(m.getSender().getPhoneNumber())
                                    .build();
                        } else {
                            // 내가 sender인 경우
                            return SummaryDetailResponse.builder()
                                    .memberId(m.getReceiver().getId().toString())
                                    .mbti(m.getReceiver().getMbti().toString())
                                    .birthdate(m.getReceiver().getBirthday().toString())
                                    .nickname(m.getReceiver().getNickname())
                                    .gender(m.getReceiver().getGender().toString())
                                    .description(m.getReceiver().getDescription())
                                    .phoneNumber(m.getReceiver().getPhoneNumber())
                                    .build();
                        }
                    })
                    .toList();

        } catch (Exception e) {
            log.error("매칭 목록 조회 중 실패: memberId={}, error={}", memberId, e.getMessage());
            return List.of();
        }
    }
}
