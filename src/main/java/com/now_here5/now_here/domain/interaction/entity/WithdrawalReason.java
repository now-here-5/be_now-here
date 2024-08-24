package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.ActiveMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "withdrawal_reason")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawalReason extends Interaction{

    @Builder
    public WithdrawalReason(String content, ActiveMember member) {
        super(content, member);
    }
}
