package com.now_here5.now_here.domain.member.entity;

import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.member.entity.role.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "active_member")
public class ActiveMember extends Member {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "sender")
    private List<Matching> sentMatchings;

    @OneToMany(mappedBy = "receiver")
    private List<Matching> receiveMatchings;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoleList;

    // 편의 메서드
    public void setEvent(Event event) {
        this.event = event;
        if (!event.getActiveMemberList().contains(this)) {
            event.getActiveMemberList().add(this);
        }
    }

    @Builder
    public ActiveMember(String token, LocalDate birthday, String phoneNumber, String nickname, String password,
                        Gender gender, Mbti mbti, String description, boolean notification, boolean status, Event event) {
        super(token, birthday, phoneNumber, nickname, password, gender, mbti, description, notification, status);
        this.event = event;
    }
}
