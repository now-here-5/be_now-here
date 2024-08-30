package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inquiry")
public class Inquiry extends Interaction {

    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    @Column(name = "answer", nullable = true)
    private String answer;

    @Builder
    public Inquiry(String content, Member member, String phoneNumber, String answer) {
        super(content, member);
        this.phoneNumber = phoneNumber;
        this.answer = answer;
    }

    // 답변을 업데이트하는 메서드
    public void updateAnswer(String answer) {
        this.answer = answer;
    }
}
