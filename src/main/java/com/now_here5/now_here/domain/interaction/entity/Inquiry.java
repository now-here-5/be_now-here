package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.global.entity.FullAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inquiry")
public class Inquiry extends Interaction {

    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    @Column(name = "answer", nullable = true)
    private String answer;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt; // 답변 시간 (답변이 있을 때만 해당)

    @Builder
    public Inquiry(String content, Member member, String phoneNumber, String answer) {
        super(content, member);
        this.phoneNumber = phoneNumber;
        this.answer = answer;
    }

    // 답변을 업데이트하는 메서드
    public void updateAnswer(String answer) {
        this.answer = answer;
        this.answeredAt = LocalDateTime.now();
    }
}
