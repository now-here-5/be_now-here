package com.now_here5.now_here.domain.matching.scheduler;

import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.MatchingStatistics;
import com.now_here5.now_here.domain.matching.entity.DailyMatchingRate;
import com.now_here5.now_here.domain.matching.entity.Status;
import com.now_here5.now_here.domain.matching.repository.MatchingRepository;
import com.now_here5.now_here.domain.matching.repository.MatchingStatisticsRepository;
import com.now_here5.now_here.domain.matching.repository.DailyMatchingRateRepository;
import com.now_here5.now_here.domain.matching.service.PreferenceBasedMBTIMatching;
import com.now_here5.now_here.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingScheduler {

    private final PreferenceBasedMBTIMatching matcher;
    private final MatchingRepository matchingRepository;
    private final MatchingStatisticsRepository matchingStatisticsRepository;
    private final DailyMatchingRateRepository dailyMatchingRateRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional  // 데이터베이스 트랜잭션 관리
    public void updateMatchingWeights() {
        log.info("Starting weight update process...");

        // 어제 날짜의 매칭 데이터를 가져옴
        List<Matching> matchings = matchingRepository.findMatchingsFromYesterday();
        int totalMatches = matchings.size();
        int acceptedMatches = 0;

        for (Matching matching : matchings) {
            Member sender = matching.getSender();
            Member receiver = matching.getReceiver();

            if (sender != null && receiver != null) {
                // 매칭 성공 여부에 따라 가중치를 업데이트
                boolean accepted = matching.getStatus() == Status.ACCEPTED;
                if (accepted) {
                    acceptedMatches++;
                }

                matcher.updatePreferences(sender.getMbti(), receiver.getMbti(), sender.getGender(), accepted);
                matcher.updatePreferences(receiver.getMbti(), sender.getMbti(), receiver.getGender(), accepted);

                // 각 성별과 MBTI별로 가중치 기록
                saveMatchingStatistics(sender, receiver);
            }
        }

        // 전체 매칭률 계산
        double matchRate = (totalMatches > 0) ? ((double) acceptedMatches / totalMatches) * 100 : 0;
        log.info("Today's matching rate: {}%", matchRate);

        // 어제의 매칭률을 가져옴
        double yesterdayMatchRate = dailyMatchingRateRepository.findByDate(LocalDate.now().minusDays(1))
                .map(DailyMatchingRate::getMatchRate)
                .orElse(0.0);

        // 매칭률 증감 계산
        double matchRateChange = matchRate - yesterdayMatchRate;

        // 전체 매칭 성공률을 기록 (하루에 한 번)
        saveDailyMatchRate(matchRate, matchRateChange);
    }

    private void saveMatchingStatistics(Member sender, Member receiver) {
        // 매칭 성공 여부에 따른 가중치 계산
        double weight = matcher.getPreferenceScore(sender.getMbti(), receiver.getMbti(), sender.getGender());

        // 매칭 통계를 기록
        MatchingStatistics statistics = MatchingStatistics.builder()
                .date(LocalDate.now())
                .userGender(sender.getGender())
                .userMbti(sender.getMbti())
                .matchedGender(receiver.getGender())
                .matchedMbti(receiver.getMbti())
                .weight(weight)
                .build();

        // 통계 저장
        matchingStatisticsRepository.save(statistics);
    }

    private void saveDailyMatchRate(double matchRate, double matchRateChange) {
        // 오늘 날짜의 통계가 이미 존재하는지 확인
        if (dailyMatchingRateRepository.existsByDate(LocalDate.now())) {
            log.info("Today's matching statistics already saved.");
            return;
        }

        // 전체 매칭률을 기록
        DailyMatchingRate dailyMatchingRate = DailyMatchingRate.builder()
                .date(LocalDate.now())  // 오늘 날짜
                .matchRate(matchRate)  // 오늘의 매칭 성공률
                .matchRateChange(matchRateChange)  // 어제와의 증감률
                .build();

        // 데이터 저장
        dailyMatchingRateRepository.save(dailyMatchingRate);
    }
}
