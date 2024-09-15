package com.now_here5.now_here.domain.matching.service;

import com.now_here5.now_here.domain.matching.converter.MatchingListToDto;
import com.now_here5.now_here.domain.matching.dto.BannerListResponse;
import com.now_here5.now_here.domain.matching.dto.MatchingWithNicknameResponse;
import com.now_here5.now_here.domain.matching.dto.NotificationResponse;
import com.now_here5.now_here.domain.matching.dto.ReceiverResponse;
import com.now_here5.now_here.domain.matching.dto.SenderResponse;
import com.now_here5.now_here.domain.matching.dto.SummaryDetailResponse;
import com.now_here5.now_here.domain.matching.dto.SummaryResponse;
import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.Status;
import com.now_here5.now_here.domain.matching.repository.MatchingRepository;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.util.AuthUtil;
import com.now_here5.now_here.infra.notification.dto.NotificationRequestDto;
import com.now_here5.now_here.infra.notification.service.FCMNotificationService;
import com.now_here5.now_here.infra.slack.service.SlackNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    private final FCMNotificationService fcmNotificationService;
    private final PreferenceBasedMBTIMatching matcher;

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
            if (!matchingRepository.existsByMembers(sender, receiver)) {
                Matching matching = Matching.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .status(Status.PENDING)
                        .build();
                receiver.updateUnreadNotiCount(receiver.getUnreadNotiCount() + 1);
                
                matchingRepository.save(matching);
                
                NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                        .title("하트가 도착했어요!")
                        .message(String.format("%s님이 %s님에게 하트를 보냈습니다.", sender.getNickname(), receiver.getNickname()))
                        .token(receiver.getFcmToken())
                        .build();
                log.info("notification message: {}", notificationRequestDto.getMessage());
//                fcmNotificationService.sendMessages(notificationRequestDto); // 알림 보내기

            } else {
                log.error("Matching already exists between {} and {}", senderId, receiverId);
            }
        } catch (Exception e) {
            log.error("Failed to send love from {} to {}: {}", senderId, receiverId, e.getMessage());
        }
    }


    @Override
    @CacheEvict(value = "bannerListCache", allEntries = true)
    public void receiveLove(Long senderId) {
        Long receiverId = authUtil.getMemberByAuthentication().getMemberId();
        Member sender = memberRepository.findActiveMemberById(senderId);
        Member receiver = memberRepository.findActiveMemberById(receiverId);

        try {
            Matching matching = matchingRepository.findBySenderAndReceiver(sender, receiver);
            if (matching != null) {
                matching.setStatus(Status.ACCEPTED);
                matchingRepository.update(matching);

                matcher.updatePreferences(sender.getMbti(), receiver.getMbti(), sender.getGender(), true);
                matcher.updatePreferences(receiver.getMbti(), sender.getMbti(), receiver.getGender(), true);

//                NotificationRequestDto rMessage = NotificationRequestDto.builder()
//                        .title("Now, Here 매칭 알림")
//                        .message(String.format("%s님과 매칭되었습니다.", sender.getNickname()))
//                        .token(receiver.getFcmToken())
//                        .build();
//                fcmNotificationService.sendMessages(rMessage);

                NotificationRequestDto sMessage = NotificationRequestDto.builder()
                        .title("Now, Here 매칭 알림")
                        .message(String.format("%s님이 하트를 수락하였습니다.", receiver.getNickname()))
                        .token(sender.getFcmToken())
                        .build();
                log.info("notification message: {}", sMessage.getMessage());
                // fcmNotificationService.sendMessages(sMessage); : TODO :  알림 보내기

                sender.updateUnreadNotiCount(receiver.getUnreadNotiCount() + 1);
//                receiver.updateUnreadNotiCount(receiver.getUnreadNotiCount() + 1);
            } else {
                log.warn("No matching found between {} and {}", senderId, receiverId);
            }
        } catch (Exception e) {
            log.error("Failed to receive love from {} to {}: {}", senderId, receiverId, e.getMessage());
        }
    }


    @Override
    public void rejectLove(Long senderId) {
        Long receiverId = authUtil.getMemberByAuthentication().getMemberId();
        Member sender = memberRepository.findActiveMemberById(senderId);
        Member receiver = memberRepository.findActiveMemberById(receiverId);

        try {
            Matching matching = matchingRepository.findBySenderAndReceiver(sender, receiver);
            if (matching != null) {
                matching.setStatus(Status.REJECTED);
                matchingRepository.update(matching);

                matcher.updatePreferences(sender.getMbti(), receiver.getMbti(), sender.getGender(), false);
                matcher.updatePreferences(receiver.getMbti(), sender.getMbti(), receiver.getGender(), false);

//                NotificationRequestDto rMessage = NotificationRequestDto.builder()
//                        .title("Now, Here")
//                        .message(String.format("%s님을 거절하셨습니다.", sender.getNickname()))
//                        .token(receiver.getFcmToken())
//                        .build();
//                fcmNotificationService.sendMessages(rMessage);
//
//                NotificationRequestDto sMessage = NotificationRequestDto.builder()
//                        .title("Now, Here")
//                        .message(String.format("%s님과 매칭에 실패했어요...", receiver.getNickname()))
//                        .token(sender.getFcmToken())
//                        .build();
//                fcmNotificationService.sendMessages(sMessage);

                sender.updateUnreadNotiCount(receiver.getUnreadNotiCount() + 1); // TODO : receiver.getUnreadNotiCount() + 1 하는게 맞는지 확인 sender?
//                receiver.updateUnreadNotiCount(receiver.getUnreadNotiCount() + 1);
            } else {
                log.warn("No matching found between {} and {}", senderId, receiverId);
            }
        } catch (Exception e) {
            log.error("Failed to reject love from {} to {}: {}", senderId, receiverId, e.getMessage());
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
                    .map(m -> {
                        Member sender = m.getSender();
                        return new SenderResponse(
                                sender.getId(),
                                sender.getMbti().toString(),
                                sender.getBirthday().toString(),
                                sender.getNickname(),
                                sender.getGender().toString(),
                                sender.getDescription());
                    })
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
                    .map(m ->
                    {
                        Member receiver = m.getReceiver();
                        return new ReceiverResponse(
                                receiver.getId(),
                                receiver.getMbti().toString(),
                                receiver.getBirthday().toString(),
                                receiver.getNickname(),
                                receiver.getGender().toString(),
                                receiver.getDescription());
                    })
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
                        Member me = m.getReceiver().getId().equals(memberId) ? m.getSender() : m.getReceiver();
                        // 내가 sender인 경우 -> m.getReceiver()
                        // 내가 receiver인 경우 -> m.getSender()

                        return SummaryDetailResponse.builder()
                                .memberId(me.getId().toString())
                                .mbti(me.getMbti().toString())
                                .birthdate(me.getBirthday().toString())
                                .nickname(me.getNickname())
                                .gender(me.getGender().toString())
                                .description(me.getDescription())
                                .phoneNumber(me.getPhoneNumber())
                                .build();
                    })
                    .toList();

        } catch (Exception e) {
            log.error("매칭 목록 조회 중 실패: memberId={}, error={}", memberId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<NotificationResponse> getNotificationList() {
        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
        Long memberId = authMember.getMemberId();
        Member member = memberRepository.findActiveMemberById(memberId);
        try {
            List<MatchingWithNicknameResponse> matchings = matchingRepository.findMatchingWithNickname(memberId);
            member.updateUnreadNotiCount(0);
            return matchings.stream()
                    .map(matching ->
                            createNotificationResponse(matching.getMatching(), matching.getCounterpartNickname(), memberId))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("알림 목록 조회 중 실패: memberId={}, error={}", memberId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public Integer getNotificationCount() {
        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
        Long memberId = authMember.getMemberId();
        Member member = memberRepository.findActiveMemberById(memberId);
        try {
            return member.getUnreadNotiCount();
        } catch (Exception e) {
            log.error("Failed to get notification count for member {}: {}", memberId, e.getMessage());
            return 0;
        }
    }

    private NotificationResponse createNotificationResponse(Matching matching, String counterpartNickname, Long memberId) {
        String title = "";
        String content = "";

        if (matching.getSender().getId().equals(memberId)) {
            // 내가 sender인 경우
            if (matching.getStatus() == Status.ACCEPTED) {
                title = "매칭 성공!";
                content = String.format("%s님과 매칭되었어요.", counterpartNickname);
            } else if (matching.getStatus() == Status.REJECTED) {
                title = "매칭 실패";
                content = String.format("%s님과의 매칭에 실패했어요.", counterpartNickname);
            }
        } else if (matching.getReceiver().getId().equals(memberId)) {
            // 내가 receiver인 경우
            if (matching.getStatus() == Status.ACCEPTED) {
                title = "매칭 성공!";
                content = String.format("%s님과 매칭되었어요.", counterpartNickname);
            } else if (matching.getStatus() == Status.PENDING) {
                title = "받은 하트";
                content = String.format("%s님이 회원님에게 하트를 보냈어요.", counterpartNickname);
            }
        }
        return NotificationResponse.builder()
                .title(title)
                .memberName(counterpartNickname)
                .content(content)
                .build();
    }
}
