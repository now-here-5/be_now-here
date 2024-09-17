package com.now_here5.now_here.infra.notification.scheduler;

import com.now_here5.now_here.domain.matching.repository.MatchingRepository;
import com.now_here5.now_here.infra.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final SmsService smsService;
    private final MatchingRepository matchingRepository;

    @Scheduled(cron = "0 0 * * * *") // 매 시간마다 실행
    public void sendNotificationToFemale() {
        log.info("Starting notification process...");
//        : TODO : 1시간 마다 매칭 요청을 받은 여성들에게 요약된 정보를 제공

//        // 지난 1시간 동안 매칭 요청을 받은 여성 회원 목록을 가져옴
//        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
//        List<Member> femaleMembers = matchingRepository.findFemaleMembersWithMatchingRequestsSince(oneHourAgo);
//
//        // 각 여성 회원에게 요약된 정보를 제공
//        for (Member femaleMember : femaleMembers) {
//            String summary = createSummaryForMember(femaleMember);
//            smsService.sendSms(femaleMember.getPhoneNumber(), summary);
//        }
//
//        log.info("Notification process completed.");
    }

//    private String createSummaryForMember(Member member) {
//        // 요약된 정보를 생성하는 로직을 구현
//        // 예시: 매칭 요청 수, 매칭된 회원 정보 등
//        int requestCount = matchingRepository.countMatchingRequestsForMember(member.getId());
//        return String.format("지난 1시간 동안 %d개의 매칭 요청이 있었습니다.", requestCount);
//    }
}
