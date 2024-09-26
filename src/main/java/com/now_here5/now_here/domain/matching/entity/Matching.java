package com.now_here5.now_here.domain.matching.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.global.entity.FullAudit;
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
        @UniqueConstraint(columnNames = {"sender_member_id", "receiver_member_id"}),
},
        indexes = {
                @Index(name = "idx_matching_status", columnList = "status"),
                @Index(name = "idx_matching_created_at", columnList = "createdAt"),
                @Index(name = "idx_sender_receiver_member_id", columnList = "sender_member_id, receiver_member_id", unique = true)
        }
)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Matching extends FullAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id", nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "sender_member_id", nullable = false)
    private Long senderMemberId;

    @Column(name = "receiver_member_id", nullable = false)
    private Long receiverMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_member_id", referencedColumnName = "member_id", insertable = false, updatable = false)

    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_member_id", referencedColumnName = "member_id", insertable = false, updatable = false)

    private Member receiver;

    @Builder
    public Matching(Member sender, Member receiver, Status status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.senderMemberId = sender.getId();
        this.receiverMemberId = receiver.getId();
    }

    public void setSender(Member sender) {
        this.sender = sender;
        if (sender != null) {
            this.senderMemberId = sender.getId();
            if (!sender.getSentMatchings().contains(this)) {
                sender.getSentMatchings().add(this);
            }
        }
    }

    public void setReceiver(Member receiver) {
        this.receiver = receiver;
        if (receiver != null) {
            this.receiverMemberId = receiver.getId();
            if (!receiver.getReceivedMatchings().contains(this)) {
                receiver.getReceivedMatchings().add(this);
            }
        }
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}