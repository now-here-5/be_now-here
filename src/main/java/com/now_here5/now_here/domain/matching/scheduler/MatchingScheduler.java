package com.now_here5.now_here.domain.matching.scheduler;

import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.PreferenceBasedMBTIMatching;
import com.now_here5.now_here.domain.matching.entity.Status;
import com.now_here5.now_here.domain.matching.repository.MatchingRepository;
import com.now_here5.now_here.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service  // Spring 서비스로 등록
@RequiredArgsConstructor  // 필수 필드를 포함하는 생성자 자동 생성 (생성자 주입)
@Slf4j  // 로깅 기능 추가
public class MatchingScheduler {

    private final PreferenceBasedMBTIMatching matcher = new PreferenceBasedMBTIMatching();  // 매칭 알고리즘 인스턴스 생성
    private final MatchingRepository matchingRepository;

    // 매일 자정에 가중치를 업데이트하는 스케줄링 메서드
    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정에 실행
    @Transactional  // 데이터베이스 트랜잭션 관리
    public void updateMatchingWeights() {
        log.info("Starting weight update process...");

        // 어제 날짜의 매칭 데이터를 가져옴
        List<Matching> matchings = matchingRepository.findMatchingsFromYesterday();

        for (Matching matching : matchings) {
            Member sender = matching.getSender();
            Member receiver = matching.getReceiver();

            if (sender != null && receiver != null) {
                // 매칭 성공 여부에 따라 가중치를 업데이트
                boolean accepted = matching.getStatus() == Status.ACCEPTED;
                matcher.updatePreferences(sender.getMbti(), receiver.getMbti(), sender.getGender(), accepted);
                matcher.updatePreferences(receiver.getMbti(), sender.getMbti(), receiver.getGender(), accepted);
            }
        }

        log.info("Weight update process completed.");
    }
}
