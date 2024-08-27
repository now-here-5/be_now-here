package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feedback")
public class Feedback extends Interaction{

    @Column(name = "field", nullable = true)
    @Min(1)  // 최소값 1
    @Max(5)  // 최대값 5
    private int field;  // 별점

    @Builder
    public Feedback(String content, Member member, int field) {
        super(content, member);
        this.field = field;
    }
}
