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
public class Inquiry extends Interaction{

    @Column(name = "answered", nullable = false)
    private boolean answered;

    @Builder
    public Inquiry(String content, Member member, boolean answered) {
        super(content, member);
        this.answered = answered;
    }


//    void updateAnswer(String answer) {
//        this.content = answer;
//    }
}
