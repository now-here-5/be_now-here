package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.ActiveMember;
import com.now_here5.now_here.global.entity.CreatedDateAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inquiry")
public class Inquiry extends Interaction{

    @Column(name = "answered", nullable = false)
    private boolean answered;

    @Builder
    public Inquiry(String content, ActiveMember member, boolean answered) {
        super(content, member);
        this.answered = answered;
    }
}
