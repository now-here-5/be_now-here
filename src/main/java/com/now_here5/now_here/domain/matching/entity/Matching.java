package com.now_here5.now_here.domain.matching.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.global.entity.CreatedDateAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "matching", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sender_member_id", "sender_active", "receiver_member_id", "receiver_active"})
})

// 자주 사용되는 데이터 2차 캐시
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Matching extends CreatedDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id", nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "sender_member_id", referencedColumnName = "member_id", insertable = false, updatable = false),
            @JoinColumn(name = "sender_active", referencedColumnName = "active", insertable = false, updatable = false)
    })
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "receiver_member_id", referencedColumnName = "member_id", insertable = false, updatable = false),
            @JoinColumn(name = "receiver_active", referencedColumnName = "active", insertable = false, updatable = false)
    })
    private Member receiver;

    @Builder
    public Matching(Status status) {
        this.status = status;
    }

    public void setSender(Member sender) {
        this.sender = sender;
        if (sender != null && !sender.getSentMatchings().contains(this)) {
            sender.getSentMatchings().add(this);
        }
    }

    public void setReceiver(Member receiver) {
        this.receiver = receiver;
        if (receiver != null && !receiver.getReceivedMatchings().contains(this)) {
            receiver.getReceivedMatchings().add(this);
        }
    }
}