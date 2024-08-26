package com.now_here5.now_here.domain.member.repository;

import com.now_here5.now_here.domain.member.entity.ActiveMember;

import java.util.List;

public interface MemberRepository {

    List<ActiveMember> findActiveMemberByPhone(String phone);

    boolean inactiveMember(Long memberId);

    ActiveMember findActiveMemberById(Long memberId);


    boolean isNickNameDuplicatedWith(String nickname, Long eventId);

    void add(ActiveMember activeMember);
}
