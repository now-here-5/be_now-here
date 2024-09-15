package com.now_here5.now_here.domain.matching.repository;

import com.now_here5.now_here.domain.matching.entity.DailyMatchingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyMatchingRateRepository extends JpaRepository<DailyMatchingRate, Long> {

    // 특정 날짜의 매칭률 데이터를 가져옴
    @Transactional(readOnly = true)
    Optional<DailyMatchingRate> findByDate(LocalDate date);

    // 특정 날짜의 매칭률 데이터가 존재하는지 확인
    @Transactional(readOnly = true)
    boolean existsByDate(LocalDate date);
}
