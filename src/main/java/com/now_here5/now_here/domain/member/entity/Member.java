package com.now_here5.now_here.domain.member.entity;

import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.member.entity.role.MemberRole;
import com.now_here5.now_here.global.entity.FullAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "phone_num"}),
        @UniqueConstraint(columnNames = {"event_id", "nick_name"})
})
public class Member extends FullAudit  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "token", nullable = true, unique = true)
    private String token;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "phone_num", nullable = false, length = 11)
    private String phoneNumber;

    @Column(name = "nick_name", nullable = false, length = 8, unique = true)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "mbti", nullable = false)
    private MBTI mbti;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "notification", nullable = false)
    private boolean notification;

    @Column(name = "popupStatus", nullable = false)
    private int popupStatus; // 하루에 몇 번 팝업이 나왔는지

    @Column(name = "unreadNotiCount", nullable = true)
    private Integer unreadNotiCount;// 읽지 않는 알림의 개수

    @Column(name = "noti_setting", nullable = false)
    private boolean notiSetting; // 알림 설정

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "sender")
    private List<Matching> sentMatchings;

    @OneToMany(mappedBy = "receiver")
    private List<Matching> receivedMatchings;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoleList;

    @Builder
    public Member(String token, LocalDate birthday, String phoneNumber, String nickname, String password,
                  Gender gender, MBTI mbti, String description, boolean notification, boolean active,
                  Event event) {
        this.token = token;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.password = password;
        this.gender = gender;
        this.mbti = mbti;
        this.description = description;
        this.notification = notification;
        this.active = active;
        this.event = event;
        this.unreadNotiCount = 0;
        this.notiSetting = true;

        // Add this member to the event's member list if it's not already present
        setEvent(event);
    }

    // 회원 수정 가능 필드용 업데이트 메서드
    public void updateToken(String newToken) {
        this.token = newToken;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updateNotification(boolean newNotification) {
        this.notification = newNotification;
    }

    public void updateMbti(MBTI mbti) {
        this.mbti = mbti;
    }

    public void updateNickName(String newNickName) {
        this.nickname = newNickName;
    }

    // 편의 메서드
    public void setEvent(Event event) {
        this.event = event;
        if (event != null && !event.getMemberList().contains(this)) {
            event.getMemberList().add(this);
        }
    }

    public void updateUnreadNotiCount(int unreadNotiCount) {
        this.unreadNotiCount = unreadNotiCount;
    }
    // 상태 관리 메서드
    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}

