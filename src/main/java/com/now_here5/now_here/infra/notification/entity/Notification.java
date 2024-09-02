package com.now_here5.now_here.infra.notification.entity;
//
//import com.now_here5.now_here.domain.member.entity.Member;
//import com.now_here5.now_here.global.entity.CreatedDateAudit;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CacheConcurrencyStrategy;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//@Getter
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@EntityListeners(AuditingEntityListener.class)
//@Table(name = "notification")
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//public class Notification extends CreatedDateAudit {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "notification_id", nullable = false, unique = true)
//    private Long id;
//
//    private String title;
//
//    private String content;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumns({
//            @JoinColumn(name = "member_id", referencedColumnName = "member_id"),
//            @JoinColumn(name = "active", referencedColumnName = "active")
//    })
//    private Member member;
//
//    @Builder
//    public Notification(String title, String content, Member member) {
//        this.title = title;
//        this.content = content;
//        this.member = member;
//    }
//
//    public void updateContent(String newContent) {
//        this.content = newContent;
//    }
//}