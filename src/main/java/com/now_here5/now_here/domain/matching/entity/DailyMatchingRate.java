package com.now_here5.now_here.domain.matching.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "daily_matching_rate")
public class DailyMatchingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;  // 날짜

    @Column(name = "match_rate", nullable = false)
    private double matchRate;  // 매칭 성공률

    @Column(name = "match_rate_change", nullable = false)
    private double matchRateChange;  // 매칭률 증감

    @Builder
    public DailyMatchingRate(LocalDate date, double matchRate, double matchRateChange) {
        this.date = date;
        this.matchRate = matchRate;
        this.matchRateChange = matchRateChange;
    }
}
