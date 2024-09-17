package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inquiry", indexes = {
        @Index(name = "idx_inquiry_member_id", columnList = "member_id"),  // 멤버 ID 인덱스
        @Index(name = "idx_inquiry_created_at", columnList = "created_at"), // 문의 생성 시간 인덱스
        @Index(name = "idx_inquiry_email", columnList = "email")  // 이메일로 조회할 가능성이 있는 경우
})
public class Inquiry extends Interaction {

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "answer")
    private String answer;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt; // 답변 시간 (답변이 있을 때만 해당)

    @Builder
    public Inquiry(String content, Member member, String email, String answer) {
        super(content, member);
        this.email = email;
        this.answer = answer;
    }

    // 답변을 업데이트하는 메서드
    public void updateAnswer(String answer) {
        this.answer = answer;
        this.answeredAt = LocalDateTime.now();
    }
}
