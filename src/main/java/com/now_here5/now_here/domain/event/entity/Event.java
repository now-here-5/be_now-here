package com.now_here5.now_here.domain.event.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.global.entity.FullAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "event", indexes = {
        @Index(name = "idx_event_status", columnList = "status"),
        @Index(name = "idx_event_period_start", columnList = "period_start"),
        @Index(name = "idx_event_period_end", columnList = "period_end"),
        @Index(name = "idx_event_location_id", columnList = "location_id"),
        @Index(name = "idx_event_status_period", columnList = "status, period_start, period_end") // 복합 인덱스
})
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
    private List<Member> memberList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false) // 장소가 없을 때는 placeless id를 가지도록 설정 (ex. -1)
    private Location location;

    @Builder
    public Event(LocalDateTime periodStart, LocalDateTime periodEnd, boolean status, String field, Location location) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.status = status;
        this.field = field;
        this.location = location;
    }

    // 통째로 바꾸기.
    public void replaceWithNewEvent(LocalDateTime periodStart, LocalDateTime periodEnd, boolean status, String field, Location location) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.status = status;
        this.field = field;
        this.location = location;
    }

    public void addMember(Member member) {
        member.setEvent(this);
        if (!this.memberList.contains(member)) {
            this.memberList.add(member);
        }
    }
}
