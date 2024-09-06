package com.now_here5.now_here.domain.matching.entity;

import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.MBTI;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "matching_statistics", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date", "user_gender", "user_mbti", "matched_gender", "matched_mbti"})
})
public class MatchingStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_gender", nullable = false)
    private Gender userGender;  // 발신자 성별

    @Enumerated(EnumType.STRING)
    @Column(name = "user_mbti", nullable = false)
    private MBTI userMbti;  // 발신자 MBTI

    @Enumerated(EnumType.STRING)
    @Column(name = "matched_gender", nullable = false)
    private Gender matchedGender;  // 수신자 성별

    @Enumerated(EnumType.STRING)
    @Column(name = "matched_mbti", nullable = false)
    private MBTI matchedMbti;  // 수신자 MBTI

    @Column(name = "weight", nullable = false)
    private Double weight;  // 가중치

    @Builder
    public MatchingStatistics(LocalDate date, Gender userGender, MBTI userMbti, Gender matchedGender, MBTI matchedMbti, Double weight) {
        this.date = date;
        this.userGender = userGender;
        this.userMbti = userMbti;
        this.matchedGender = matchedGender;
        this.matchedMbti = matchedMbti;
        this.weight = weight;
    }
}
