package com.now_here5.now_here.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class FullAudit extends CreatedDateAudit{

    @LastModifiedDate
    @Column(name = "last_modified_at")
    private LocalDateTime modifiedAt;
}