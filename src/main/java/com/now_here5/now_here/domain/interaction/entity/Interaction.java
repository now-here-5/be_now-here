
package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.global.entity.CreatedDateAudit;
import com.now_here5.now_here.global.entity.FullAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Interaction extends CreatedDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interation_id", nullable = false, updatable = true)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT) ) // 외래 키 제약 조건을 생성하지 않음
    private Member member;


    public Interaction(String content, Member member) {
        this.content = content;
        this.member = member;
    }
}