package com.now_here5.now_here.domain.event.entity;

import com.now_here5.now_here.domain.member.entity.ActiveMember;
import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.InactiveMember;
import com.now_here5.now_here.domain.member.entity.Mbti;
import com.now_here5.now_here.global.entity.FullAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "event")
public class Event extends FullAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "period_start", nullable = false)
    private LocalDateTime periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDateTime periodEnd;

    @Column(name = "status", nullable = false)
    private boolean status;

    @Column(name = "field", nullable = false)
    private String field;

    @OneToMany(mappedBy = "event",  cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActiveMember> activeMemberList = new ArrayList<>();

    @OneToMany(mappedBy = "event",  cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InactiveMember> inactiveMemberList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Builder
    public Event(LocalDateTime periodStart, LocalDateTime periodEnd, boolean status, String field, Location location) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.status = status;
        this.field = field;
        this.location = location;
    }

    public void addActiveMember(ActiveMember activeMember) {
        activeMember.setEvent(this);
        if (!this.activeMemberList.contains(activeMember)) {
            this.activeMemberList.add(activeMember);
        }
    }
    public void addInactiveMember(InactiveMember inactiveMember) {
        inactiveMember.setEvent(this);
        if (!this.inactiveMemberList.contains(inactiveMember)) {
            this.inactiveMemberList.add(inactiveMember);
        }
    }
}
