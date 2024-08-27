package com.now_here5.now_here.domain.member.repository;

import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.Member;

import java.util.List;

public interface MemberRepository {

    List<Member> findActiveMemberByPhone(String phone);

    boolean inactiveMember(Long memberId);

    Member findActiveMemberById(Long memberId);


    boolean isNickNameDuplicatedWith(String nickname, Long eventId);

    void add(Member activeMember);

    Member findMemberById(Long memberId);

    List<Member> findMembersByEventIdAndGender(Long eventId, Gender gender);
}
