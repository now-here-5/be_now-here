package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.ActiveMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    private int field;

    @Builder
    public Feedback(String content, ActiveMember member, int field) {
        super(content, member);
        this.field = field;
    }
}
