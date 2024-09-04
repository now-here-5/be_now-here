package com.now_here5.now_here.domain.matching.service;
import com.now_here5.now_here.domain.member.entity.MBTI;
import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.Member;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PreferenceBasedMBTIMatching {
    // 남성과 여성 각각의 MBTI 선호도를 저장하는 ConcurrentHashMap
    private final Map<MBTI, Map<MBTI, Double>> malePreferences;
    private final Map<MBTI, Map<MBTI, Double>> femalePreferences;

    // 생성자에서 ConcurrentHashMap으로 초기화
    public PreferenceBasedMBTIMatching() {
        malePreferences = new ConcurrentHashMap<>();
        femalePreferences = new ConcurrentHashMap<>();
        initializeWeights();  // 초기 가중치 설정 메서드 호출
    }

    // 가중치 초기화를 ConcurrentHashMap으로 진행
    private void initializeWeights() {
        MBTI[] mbtiTypes = MBTI.values();
        for (MBTI mbti : mbtiTypes) {
            malePreferences.putIfAbsent(mbti, new ConcurrentHashMap<>());
            femalePreferences.putIfAbsent(mbti, new ConcurrentHashMap<>());
            for (MBTI otherMbti : mbtiTypes) {
                malePreferences.get(mbti).put(otherMbti, 0.5);
                femalePreferences.get(mbti).put(otherMbti, 0.5);
            }
        }
    }

    // 특정 MBTI와 성별에 대한 선호도 점수를 반환하는 메서드
    public double getPreferenceScore(MBTI userMbti, MBTI potentialMatchMbti, Gender gender) {
        // 성별에 따라 남성 또는 여성의 선호도 맵에서 값을 가져옴
        Map<MBTI, Map<MBTI, Double>> preferences = gender == Gender.MALE ? malePreferences : femalePreferences;

        preferences.putIfAbsent(userMbti, new HashMap<>());  // 성별에 맞는 사용자 MBTI가 없을 경우 새로 추가
        return preferences.get(userMbti).getOrDefault(potentialMatchMbti, 0.5);
    }

    // 매칭 성공 여부에 따라 선호도 가중치를 업데이트하는 메서드
    public void updatePreferences(MBTI userMbti, MBTI matchedMbti, Gender gender, boolean success) {
        // 성별에 따라 남성 또는 여성의 선호도 맵에서 값을 가져옴
        Map<MBTI, Map<MBTI, Double>> preferences = gender == Gender.MALE ? malePreferences : femalePreferences;
        preferences.putIfAbsent(userMbti, new HashMap<>());  // NullPointerException 방지

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
                continue;  // 매칭 상대의 MBTI나 성별이 null일 경우 해당 매칭을 건너뜀
            }

            // 사용자와 잠재적 매칭 상대의 선호도 점수를 계산
            double userPref = getPreferenceScore(userMbti, match.getMbti(), userGender);
            double matchPref = getPreferenceScore(match.getMbti(), userMbti, match.getGender());

            // 매칭 점수를 두 선호도의 평균 대신 최소값으로 사용
            double score = Math.min(userPref, matchPref);  // 선호도가 낮은 쪽을 기준으로 매칭 점수를 계산
            log.info("Matched score: {}", score);

            results.add(new MatchResult(match, score));
        }

        // 점수를 기준으로 내림차순 정렬
        results.sort((a, b) -> Double.compare(b.score, a.score));

        // 상위 두 개만 반환
        return results.stream()
                .limit(2)  // 상위 2개의 결과만 반환
                .collect(Collectors.toList());
    }



    // 잠재적인 매칭 상대를 표현하는 내부 클래스
    public static class PotentialMatch {
        Member member;  // Member 객체를 필드로 추가

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
            return String.format(
                    "(MBTI: %s, Gender: %s, Score: %.2f)",
                    match.getMbti(),  // Member 객체의 MBTI를 가져옴
                    match.getGender(),  // Member 객체의 성별을 가져옴
                    score
            );
        }
    }

}
