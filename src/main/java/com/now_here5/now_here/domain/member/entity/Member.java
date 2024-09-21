package com.now_here5.now_here.domain.member.entity;

import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.member.entity.role.MemberRole;
import com.now_here5.now_here.global.entity.FullAudit;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "phone_number"}), // 복합 유니크 제약 조건
        @UniqueConstraint(columnNames = {"event_id", "nick_name"})
},
        indexes = {
                @Index(name = "idx_event_id", columnList = "event_id"), // event_id 인덱스
                @Index(name = "idx_phone_number", columnList = "phone_number"), // 전화번호 인덱스
                @Index(name = "idx_nick_name", columnList = "nick_name") // 닉네임 인덱스
        }
)
public class Member extends FullAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "phone_number", nullable = true, length = 11)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "nick_name", nullable = false, length = 8)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "mbti", nullable = false)
    private MBTI mbti;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "popupCount", nullable = false)
    private int popupCount; // 하루에 몇번이나 매칭 페이지를 조회했는지

    @Column(name = "unreadNotiCount")
    private Integer unreadNotiCount;// 읽지 않는 알림의 개수

    @Column(name = "special_heart", nullable = false)
    private int specialHeart; // 특별 하트 개수

    @Column(name = "noti_setting", nullable = false)
    private boolean notiSetting; // 알림 설정

    @Column(name = "active", nullable = false)
    private boolean active;

    // 연관관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "sender")
    private List<Matching> sentMatchings = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<Matching> receivedMatchings = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoleList = new ArrayList<>();

    @Builder
    public Member(String token, LocalDate birthday, String phoneNumber, String nickname,
                  String password, Gender gender, MBTI mbti, String description,
                  boolean active, Event event) {
        this.token = token;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.password = password;
        this.gender = gender;
        this.mbti = mbti;
        this.description = description;
        this.active = active;
        this.event = event;

        //default
        this.unreadNotiCount = 0;
        this.notiSetting = true;
        this.popupCount = 0;
        this.specialHeart =10; // 10번 무료로 알림 보낼 수 있음.

        setEvent(event);
    }

    // 회원 수정 가능 필드용 업데이트 메서드
    public void updateToken(String newToken) {
        this.token = newToken;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updateNotiSetting(boolean notiSetting) {
        this.notiSetting = notiSetting;
    }

    public int updatePopupCount(int num) {
        this.popupCount = num;
        return this.popupCount;
    }

    public void updateMbti(MBTI mbti) {
        this.mbti = mbti;
    }

    public void updateNickName(String newNickName) {
        this.nickname = newNickName;
    }

    public void updateBirthday(LocalDate newBirthday) {
        this.birthday = newBirthday;
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

    public void updateSpecialHeartAndUnReadNotiCount(int specialHeart, int unreadNotiCount) {
        this.specialHeart = specialHeart;
        updateUnreadNotiCount(unreadNotiCount);
    }

    // 상태 관리 메서드
    public void activate() {
        this.active = true;
    }

}

