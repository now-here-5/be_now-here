package com.now_here5.now_here.domain.matching.entity;

import com.now_here5.now_here.domain.member.entity.ActiveMember;
import com.now_here5.now_here.domain.member.entity.InactiveMember;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.global.entity.CreatedDateAudit;
import com.now_here5.now_here.global.entity.FullAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "matching", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sender_id", "receiver_id"})
})
public class Matching extends CreatedDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id", nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Builder
    public Matching(Status status) {
        this.status = status;
    }

    public void setSender(Member sender) {
        this.sender = sender;
        if (sender instanceof ActiveMember) {
            ActiveMember activeSender = (ActiveMember) sender;
            if (!activeSender.getSentMatchings().contains(this)) {
                activeSender.getSentMatchings().add(this);
            }
        } else if (sender instanceof InactiveMember) {
            InactiveMember inactiveSender = (InactiveMember) sender;
            if (!inactiveSender.getSentMatchings().contains(this)) {
                inactiveSender.getSentMatchings().add(this);
            }
        }
    }

    public void setReceiver(Member receiver) {
        this.receiver = receiver;
        if (receiver instanceof ActiveMember) {
            ActiveMember activeReceiver = (ActiveMember) receiver;
            if (!activeReceiver.getReceiveMatchings().contains(this)) {
                activeReceiver.getReceiveMatchings().add(this);
            }
        } else if (receiver instanceof InactiveMember) {
            InactiveMember inactiveReceiver = (InactiveMember) receiver;
            if (!inactiveReceiver.getReceivedMatchings().contains(this)) {
                inactiveReceiver.getReceivedMatchings().add(this);
            }
        }
    }
}
