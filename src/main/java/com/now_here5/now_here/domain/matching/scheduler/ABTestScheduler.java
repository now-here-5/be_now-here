//package com.now_here5.now_here.domain.matching.scheduler;
//
//import com.now_here5.now_here.domain.matching.entity.PreferenceBasedMBTIMatching;
//import com.now_here5.now_here.domain.member.entity.Member;
//import com.now_here5.now_here.domain.member.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.regex.MatchResult;
//
//@Service  // Spring 서비스로 등록
//@RequiredArgsConstructor  // 필수 필드를 포함하는 생성자 자동 생성 (생성자 주입)
//@Slf4j  // 로깅 기능 추가
//public class ABTestScheduler {
//
//    private final MemberRepository memberRepository;
//    private final PreferenceBasedMBTIMatching matcher = new PreferenceBasedMBTIMatching();  // 매칭 알고리즘 인스턴스 생성
//
//    // 매일 자정에 A/B 테스트를 수행하는 스케줄링 메서드
//    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정에 실행
//    public void runABTest() {
//        log.info("Starting A/B test process...");
//
//        // 현재 활성화된 모든 멤버 리스트를 가져옴
//        List<Member> members = memberRepository.findAllActiveMembers();
//
//        for (Member member : members) {
//            // 각 멤버에 대해 잠재적인 매칭 상대를 찾음
//            List<Member> potentialMatches = memberRepository.findPotentialMatches(member.getId(), member.getEvent().getId(), member.getGender());
//            List<PreferenceBasedMBTIMatching.MatchResult> matchResults = matcher.findMatches(
//                    member.getMbti().name(),
//                    member.getGender().name(),
//                    potentialMatches.stream().map(m -> new PreferenceBasedMBTIMatching.PotentialMatch(m.getMbti().name(), m.getGender().name())).toList()
//            );
//
//            // A/B 테스트 결과를 비교하고 저장
//            compareAndSaveABTestResults(member, matchResults);
//        }
//
//        log.info("A/B test process completed.");
//    }
//
////    // A/B 테스트 결과를 비교하고 저장하는 메서드
////    private void compareAndSaveABTestResults(Member member, List<PreferenceBasedMBTIMatching.MatchResult> matchResults) {
////        // 1. 기존 알고리즘(A)의 결과와 비교
////        List<MatchResult> existingAlgorithmResults = existingMatchingAlgorithm.findMatches(member);
////
////        // 2. 결과 비교 및 분석
////        MatchingComparisonResult comparisonResult = analyzeResults(existingAlgorithmResults, matchResults);
////
////        // 3. 분석 결과 저장
////        abTestResultRepository.save(new ABTestResult(member, comparisonResult));
////
////        // 4. 로그 기록
////        log.info("A/B test for member {}: Algorithm A score: {}, Algorithm B score: {}",
////                member.getId(), comparisonResult.getAlgorithmAScore(), comparisonResult.getAlgorithmBScore());
////
////        // 5. 필요한 경우 알림 발송
////        if (comparisonResult.isSignificantDifference()) {
////            notificationService.sendAlertToDataScientists(comparisonResult);
////        }
////    }
//}
