package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.ActiveMember;
import com.now_here5.now_here.global.entity.CreatedDateAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class Interaction extends CreatedDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interation_id", nullable = false, updatable = true)
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private ActiveMember member;

    public Interaction( String content, ActiveMember member) {
        this.content = content;
        this.member = member;
    }
}
