package com.now_here5.now_here.domain.interaction.entity;

import com.now_here5.now_here.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "withdrawal_reason", indexes = {
        @Index(name = "idx_withdrawal_reason_member_id", columnList = "member_id"),  // 멤버 ID 인덱스
        @Index(name = "idx_withdrawal_reason_created_at", columnList = "created_at") // 생성 시간 인덱스
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawalReason extends Interaction{

    @Builder
    public WithdrawalReason(String content, Member member) {
        super(content, member);
    }
}
