package com.now_here5.now_here.domain.member.entity;

import com.now_here5.now_here.global.entity.FullAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member")
public abstract class Member extends FullAudit {

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
    private Mbti mbti;

    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "notification", nullable = false)
    private boolean notification;

    @Column(name = "status", nullable = false)
    private boolean status;

    @Column(name = "checkNoti", nullable = false)
    private LocalDateTime checkNotiTime;

    @Column(name = "noti_setting", nullable = false)
    private boolean notiSetting;

    protected Member(String token, LocalDate birthday, String phoneNumber, String nickname, String password,
                     Gender gender, Mbti mbti, String description, boolean notification, boolean status) {
        this.token = token;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.password = password;
        this.gender = gender;
        this.mbti = mbti;
        this.description = description;
        this.notification = notification;
        this.status = status;
        // notiSetting을 초기화합니다.
        this.notiSetting = true;
    }

    @PrePersist
    protected void onPrePersist() {
        this.checkNotiTime = this.getCreatedAt();  // 엔티티가 영속화되기 직전에 checkNotiTime을 설정
    }

    public void updateToken(String token) {
        this.token = token;
    }


}

