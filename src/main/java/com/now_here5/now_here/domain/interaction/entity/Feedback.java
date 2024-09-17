package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feedback", indexes = {
        @Index(name = "idx_feedback_member_id", columnList = "member_id"),  // 멤버 ID 인덱스
        @Index(name = "idx_feedback_created_at", columnList = "created_at"), // 피드백 생성 시간 인덱스
        @Index(name = "idx_feedback_member_created", columnList = "member_id, created_at") // 복합 인덱스
})
public class Feedback extends Interaction{

    @Column(name = "field")
//    @Min(1)  // 최소값 1
    @Max(5)  // 최대값 5
    private int field;  // 별점


    @Builder
    public Feedback(String content, Member member, int field) {
        super(content, member);
        this.field = field;
    }
}
