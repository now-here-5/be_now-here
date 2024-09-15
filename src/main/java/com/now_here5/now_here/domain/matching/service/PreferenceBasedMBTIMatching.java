package com.now_here5.now_here.domain.matching.service;

import com.now_here5.now_here.domain.matching.entity.MatchingStatistics;
import com.now_here5.now_here.domain.matching.repository.MatchingStatisticsRepository;
import com.now_here5.now_here.domain.member.entity.MBTI;
import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor  // 생성자 주입을 위한 Lombok 애노테이션
public class PreferenceBasedMBTIMatching {

    private final MatchingStatisticsRepository matchingStatisticsRepository;

    // 남성과 여성 각각의 MBTI 선호도를 저장하는 ConcurrentHashMap
    private final Map<MBTI, Map<MBTI, Double>> malePreferences = new ConcurrentHashMap<>();
    private final Map<MBTI, Map<MBTI, Double>> femalePreferences = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeWeights() {
        MBTI[] mbtiTypes = MBTI.values();

        // 어제의 날짜를 구함
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // DB에서 어제 날짜의 가중치만 불러오기
        List<MatchingStatistics> yesterdayStatistics = matchingStatisticsRepository.findByDate(yesterday);

        // 남성과 여성의 가중치 맵 초기화
        for (MBTI mbti : mbtiTypes) {
            malePreferences.putIfAbsent(mbti, new ConcurrentHashMap<>());
            femalePreferences.putIfAbsent(mbti, new ConcurrentHashMap<>());
        }

        // 어제 데이터를 메모리에서 처리하여 남성 및 여성에 대한 가중치 설정
        for (MatchingStatistics stat : yesterdayStatistics) {
            MBTI userMbti = stat.getUserMbti();
            MBTI matchedMbti = stat.getMatchedMbti();
            double weight = stat.getWeight();

            if (stat.getUserGender() == Gender.MALE) {
                malePreferences.get(userMbti).put(matchedMbti, weight);
            } else if (stat.getUserGender() == Gender.FEMALE) {
                femalePreferences.get(userMbti).put(matchedMbti, weight);
            }
        }

        // 없는 경우 기본값으로 0.5를 설정
        for (MBTI mbti : mbtiTypes) {
            for (MBTI otherMbti : mbtiTypes) {
                malePreferences.get(mbti).putIfAbsent(otherMbti, 0.5);
                femalePreferences.get(mbti).putIfAbsent(otherMbti, 0.5);
            }
        }
    }


    // 특정 MBTI와 성별에 대한 선호도 점수를 반환하는 메서드
    public double getPreferenceScore(MBTI userMbti, MBTI potentialMatchMbti, Gender gender) {
        Map<MBTI, Map<MBTI, Double>> preferences = (gender == Gender.MALE) ? malePreferences : femalePreferences;
        preferences.putIfAbsent(userMbti, new EnumMap<>(MBTI.class));
        return preferences.get(userMbti).getOrDefault(potentialMatchMbti, 0.5);
    }

    // @Async : 비동기적으로 메서드를 실행하도록 지정하면, 성능을 개선할 가능성이 큼.
    // 매칭 성공 여부에 따라 선호도 가중치를 업데이트하는 메서드
    public void updatePreferences(MBTI userMbti, MBTI matchedMbti, Gender gender, boolean success) {
        Map<MBTI, Map<MBTI, Double>> preferences = (gender == Gender.MALE) ? malePreferences : femalePreferences;
        preferences.putIfAbsent(userMbti, new EnumMap<>(MBTI.class));

        // 동적 조정법 : 가중치가 극단에 갈수록 안정성을 높임
        double baseAdjustment = 0.1;
        double currentWeight = preferences.get(userMbti).getOrDefault(matchedMbti, 0.5);
        double dynamicAdjustment = baseAdjustment * (1 - Math.abs(currentWeight - 0.5));
        double adjustment = success ? dynamicAdjustment : -dynamicAdjustment;

        // 상한선과 하한선 설정 (최소 0.2, 최대 0.8)
        double newWeight = Math.max(0.2, Math.min(0.8, currentWeight + adjustment));
        preferences.get(userMbti).put(matchedMbti, newWeight);
    }

    public List<MatchResult> findMatches(MBTI userMbti, Gender userGender, List<PotentialMatch> potentialMatches) {
        List<MatchResult> results = new ArrayList<>();

        if (userMbti == null || userGender == null) {
            throw new IllegalArgumentException("User MBTI or gender cannot be null");
        }

        for (PotentialMatch match : potentialMatches) {
            if (match.getMbti() == null || match.getGender() == null) {
                continue;
            }

            double userPref = getPreferenceScore(userMbti, match.getMbti(), userGender);
            double matchPref = getPreferenceScore(match.getMbti(), userMbti, match.getGender());
            double score = Math.min(userPref, matchPref);
            log.info("Matched score: {}", score);
            results.add(new MatchResult(match, score));
        }

        results.sort((a, b) -> Double.compare(b.score, a.score));

        return results.stream().limit(2).collect(Collectors.toList());
    }

    // 잠재적인 매칭 상대방을 표현하는 내부 클래스
    public static class PotentialMatch {
        Member member;

        public PotentialMatch(Member member) {
            this.member = member;
        }

        public MBTI getMbti() {
            return member.getMbti();
        }

        public Gender getGender() {
            return member.getGender();
        }

        public Long getId() {
            return member.getId();
        }

        public String getNickname() {
            return member.getNickname();
        }

        public LocalDate getBirthday() {
            return member.getBirthday();
        }

        public String getDescription() {
            return member.getDescription();
        }
    }

    // 매칭 결과를 표현하는 내부 클래스
    public static class MatchResult {
        @Getter
        PotentialMatch match;
        double score;

        public MatchResult(PotentialMatch match, double score) {
            this.match = match;
            this.score = score;
        }

        @Override
        public String toString() {
            return String.format("(MBTI: %s, Gender: %s, Score: %.2f)", match.getMbti(), match.getGender(), score);
        }
    }
}
