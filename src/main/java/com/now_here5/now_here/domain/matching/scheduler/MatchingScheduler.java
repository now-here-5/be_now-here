package com.now_here5.now_here.domain.matching.scheduler;

import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.MatchingStatistics;
import com.now_here5.now_here.domain.matching.entity.DailyMatchingRate;
import com.now_here5.now_here.domain.matching.entity.Status;
import com.now_here5.now_here.domain.matching.repository.MatchingRepository;
import com.now_here5.now_here.domain.matching.repository.MatchingStatisticsRepository;
import com.now_here5.now_here.domain.matching.repository.DailyMatchingRateRepository;
import com.now_here5.now_here.domain.matching.service.PreferenceBasedMBTIMatching;
import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.MBTI;
import com.now_here5.now_here.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class MatchingScheduler {

    private final PreferenceBasedMBTIMatching matcher;
    private final MatchingRepository matchingRepository;
    private final MatchingStatisticsRepository matchingStatisticsRepository;
    private final DailyMatchingRateRepository dailyMatchingRateRepository;

    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시에 실행
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
            }
        }

        // 각 성별과 MBTI 조합에 대한 가중치를 저장 (남자 -> 여자, 여자 -> 남자 경우만)
        for (MBTI userMbti : MBTI.values()) {
            for (MBTI matchedMbti : MBTI.values()) {
                // 남자 -> 여자 경우
                double weightMaleToFemale = matcher.getPreferenceScore(userMbti, matchedMbti, Gender.MALE);
                saveMatchingStatistics(Gender.MALE, userMbti, Gender.FEMALE, matchedMbti, weightMaleToFemale);

                // 여자 -> 남자 경우
                double weightFemaleToMale = matcher.getPreferenceScore(userMbti, matchedMbti, Gender.FEMALE);
                saveMatchingStatistics(Gender.FEMALE, userMbti, Gender.MALE, matchedMbti, weightFemaleToMale);
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

    @Async
    protected void saveMatchingStatistics(Gender userGender, MBTI userMbti, Gender matchedGender, MBTI matchedMbti, double weight) {

            // 새로운 통계 저장
            MatchingStatistics statistics = MatchingStatistics.builder()
                    .date(LocalDate.now())
                    .userGender(userGender)
                    .userMbti(userMbti)
                    .matchedGender(matchedGender)
                    .matchedMbti(matchedMbti)
                    .weight(weight)
                    .build();
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
