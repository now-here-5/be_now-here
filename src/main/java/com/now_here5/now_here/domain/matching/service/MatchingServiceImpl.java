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
import com.now_here5.now_here.global.util.CustomXOR;
import com.now_here5.now_here.infra.notification.dto.SmsRequest;
import com.now_here5.now_here.infra.notification.service.NotificationService;
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
    private final NotificationService notificationService;
    private final PreferenceBasedMBTIMatching matcher;
    private final CustomXOR xor;


    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "bannerListCache")// 메서드의 결과를 캐시하여 동일한 인자로 호출되면 캐시된 결과 반환
    @Override
    public List<BannerListResponse> getBannerList() {
        // 최적화된 쿼리로 데이터를 가져옴
        List<Object[]> results = matchingRepository.findMemberForBanner(Status.ACCEPTED);

        // 데이터를 DTO로 변환하여 반환
        return matchingListToDto.convertToBannerListResponse(results);
    }

    @Transactional
    @Override
    public boolean sendLove(Long receiverId, boolean isSpecialUsed) {
        Long senderId = authUtil.getMemberByAuthentication().getMemberId();
        Member sender = memberRepository.findActiveMemberById(senderId);
        Member receiver = memberRepository.findActiveMemberById(receiverId);

        // 존재하는 매칭 확인
        if (matchingRepository.isExistsByMemberIds(sender.getId(), receiver.getId())) {
            throw new IllegalArgumentException("Matching already exists");
        }

        Matching matching = createMatching(sender, receiver);
        matchingRepository.save(matching);
        receiver.updateUnreadNotiCount(receiver.getUnreadNotiCount() + 1);

        if (isSpecialUsed && receiver.isNotiSetting()) { // when receiver has allowed notification
            handleSpecialHeart(sender, receiver);
            return true;
        } else return !isSpecialUsed;
    }

    @Transactional
    @Override
    public boolean removeHeart(Long receiverId) {
        try {
            AuthenticatedMemberDto member = authUtil.getMemberByAuthentication();
            matchingRepository.removeSentHeart(member.getMemberId(), receiverId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Matching createMatching(Member sender, Member receiver) {
        return Matching.builder()
                .sender(sender)
                .receiver(receiver)
                .status(Status.PENDING)
                .build();
    }

    private void handleSpecialHeart(Member sender, Member receiver) {
        Long eventId = receiver.getEvent().getId();
        //String encryptEventId = xor.encrypt(eventId);
        SmsRequest smsRequest = createSmsRequest(receiver, eventId.toString());

        if (sender.getSpecialHeart() <= 0) {
            throw new IllegalArgumentException("Special heart is not enough");
        }
        sender.updateSpecialHeart(sender.getSpecialHeart() - 1);

        notificationService.sendSms(smsRequest);
    }

    private SmsRequest createSmsRequest(Member receiver, String eventId) {
        String url = switch (eventId) {
            case "1":
                yield "https://www.now-here.site/match/received-hearts?eventCode=MTAyOTM4NDY";
            case "2":
                yield "https://나우히어.lrl.kr";
            case "3":
                yield "https://나우히어_가을.lrl.kr";


            default:
                throw new IllegalStateException("Unexpected value: " + eventId);
        };

        return SmsRequest.builder()
                .message("하트가 도착했어요! 지금 확인하고 응답해보세요 : " + url)
                .phoneNumber(receiver.getPhoneNumber())
                .build();
    }


    @Override
    @CacheEvict(value = "bannerListCache", allEntries = true)
    @Transactional
    public void receiveLove(Long senderId) {
        Long receiverId = authUtil.getMemberByAuthentication().getMemberId();
        Member sender = memberRepository.findActiveMemberById(senderId);
        Member receiver = memberRepository.findActiveMemberById(receiverId);
        Long eventId = sender.getEvent().getId();
        //String encryptEventId = xor.encrypt(eventId);

        String url = switch (eventId.toString()) {
            case "1":
                yield "https://www.now-here.site/match/status?eventCode=MTAyOTM4NDY";
            case "2":
                yield "https://_나우히어.lrl.kr";
            case "3":
                yield "https://가을_나우히어.lrl.kr";

            default:
                throw new IllegalStateException("Unexpected value: " + eventId);
        };
        try {
            Matching matching = matchingRepository.findBySenderAndReceiver(sender.getId(), receiver.getId());
            if (matching != null) {

                matching.setStatus(Status.ACCEPTED);
                matchingRepository.update(matching);

                SmsRequest smsRequest = SmsRequest.builder()
                        .message("매칭되었습니다! 지금 바로 상대와 연락을 시작해보세요 : " + url)
                        .phoneNumber(sender.getPhoneNumber())
                        .build();

                sender.updateUnreadNotiCount(sender.getUnreadNotiCount() + 1);

                // send notification to the person who sent heart first
                // if the person has allowed notification
                if (sender.isNotiSetting()) notificationService.sendSms(smsRequest);

                matcher.updatePreferences(sender.getMbti(), receiver.getMbti(), sender.getGender(), true);
                matcher.updatePreferences(receiver.getMbti(), sender.getMbti(), receiver.getGender(), true);
            } else {
                log.warn("No matching found between {} and {}", senderId, receiverId);
            }
        } catch (Exception e) {
            log.error("Failed to receive love from {} to {}: {}", senderId, receiverId, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public void rejectLove(Long senderId) {
        Long receiverId = authUtil.getMemberByAuthentication().getMemberId();
        Member sender = memberRepository.findActiveMemberById(senderId);
        Member receiver = memberRepository.findActiveMemberById(receiverId);

        try {
            Matching matching = matchingRepository.findBySenderAndReceiver(sender.getId(), receiver.getId());
            if (matching != null) {
                matching.setStatus(Status.REJECTED);
                matchingRepository.update(matching);

                sender.updateUnreadNotiCount(sender.getUnreadNotiCount() + 1);
                matcher.updatePreferences(sender.getMbti(), receiver.getMbti(), sender.getGender(), false);
                matcher.updatePreferences(receiver.getMbti(), sender.getMbti(), receiver.getGender(), false);
            } else {
                log.warn("No matching found between {} and {}", senderId, receiverId);
            }
        } catch (Exception e) {
            log.error("Failed to reject love from {} to {}: {}", senderId, receiverId, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<SummaryResponse> getSummary() {
        Long targetId = authUtil.getMemberByAuthentication().getMemberId();
        Long receiveLove = matchingRepository.countByReceiverId(targetId);
        Long sendLove = matchingRepository.countBySenderId(targetId);

        return List.of(SummaryResponse.builder()
                .receiveLove(String.valueOf(receiveLove))
                .sendLove(String.valueOf(sendLove))
                .build());
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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
                        // 내가 sender인 경우 -> m.getReceiver()
                        // 내가 receiver인 경우 -> m.getSender()
                        Member me = m.getReceiver().getId().equals(memberId) ? m.getSender() : m.getReceiver();

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

    @Transactional
    @Override
    public List<NotificationResponse> getNotificationList() { // TODO : 페이징 필수 - 알림을 직접 만들어야 하기 때문에 많이 조회될 수록 불리함.
        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
        Long memberId = authMember.getMemberId();
        Member member = memberRepository.findActiveMemberById(memberId);
        try {
            List<MatchingWithNicknameResponse> matchings = matchingRepository.findMatchingWithNickname(memberId);
            member.updateUnreadNotiCount(0);
            return matchings.stream()
                    .map(matching ->
                            createNotificationResponse(matching, memberId))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("알림 목록 조회 중 실패: memberId={}, error={}", memberId, e.getMessage());
            return List.of();
        }
    }

    @Transactional(readOnly = true)
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


    @Transactional(readOnly = true)
    @Override
    public Integer getSpecialHeartCount() {
        try {
            AuthenticatedMemberDto member = authUtil.getMemberByAuthentication();
            return memberRepository.getSpecialHeartCountByMemberId(member.getMemberId());
        } catch (Exception e) {
            log.error("Failed to get special heart count: {}", e.getMessage());
            return null;
        }
    }

    private NotificationResponse createNotificationResponse(MatchingWithNicknameResponse matching, Long memberId) {
        String title = getTitle(matching, memberId);
        String content = getContent(matching, memberId);

        return NotificationResponse.builder()
                .title(title)
                .memberName(matching.getCounterpartNickname())
                .content(content)
                .build();
    }

    private String getTitle(MatchingWithNicknameResponse matching, Long memberId) {
        if (matching.getMatching().getSender().getId().equals(memberId)) {
            if (matching.getMatching().getStatus() == Status.ACCEPTED) {
                return "매칭 성공!";
            } else if (matching.getMatching().getStatus() == Status.REJECTED) {
                return "매칭 실패";
            }
        } else if (matching.getMatching().getReceiver().getId().equals(memberId)) {
            if (matching.getMatching().getStatus() == Status.ACCEPTED) {
                return "매칭 성공!";
            } else if (matching.getMatching().getStatus() == Status.PENDING) {
                return "받은 하트";
            }
        }
        return "";
    }

    private String getContent(MatchingWithNicknameResponse matching, Long memberId) {
        if (matching.getMatching().getSender().getId().equals(memberId)) {
            if (matching.getMatching().getStatus() == Status.ACCEPTED) {
                return String.format("%s님과 매칭되었어요.", matching.getCounterpartNickname());
            } else if (matching.getMatching().getStatus() == Status.REJECTED) {
                return String.format("%s님과의 매칭에 실패했어요.", matching.getCounterpartNickname());
            }
        } else if (matching.getMatching().getReceiver().getId().equals(memberId)) {
            if (matching.getMatching().getStatus() == Status.ACCEPTED) {
                return String.format("%s님과 매칭되었어요.", matching.getCounterpartNickname());
            } else if (matching.getMatching().getStatus() == Status.PENDING) {
                return String.format("%s님이 회원님에게 하트를 보냈어요.", matching.getCounterpartNickname());
            }
        }
        return "";
    }
}
