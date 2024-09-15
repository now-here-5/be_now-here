package com.now_here5.now_here.domain.matching.repository;

import com.now_here5.now_here.domain.matching.entity.MatchingStatistics;
import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.MBTI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface MatchingStatisticsRepository extends JpaRepository<MatchingStatistics, Long> {

    // 특정 날짜의 매칭 통계가 이미 존재하는지 확인하는 메서드
    boolean existsByDate(LocalDate date);

    // 특정 날짜의 매칭 통계를 찾는 메서드
    List<MatchingStatistics> findByDate(LocalDate date);

    // 모든 매칭 통계를 날짜별로 정렬해서 가져오는 메서드 (선택 사항)
    // 기본적으로 JpaRepository가 제공하는 findAll() 메서드를 날짜별로 정렬해 사용할 수 있음
    List<MatchingStatistics> findAllByOrderByDateDesc();

    // 특정 성별과 MBTI에 대한 통계 조회
    List<MatchingStatistics> findByUserGenderAndUserMbti(Gender userGender, MBTI userMbti);
}
