package com.now_here5.now_here.domain.member.repository;


import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.Member;
import java.util.List;

public interface MemberRepository {

    List<Member> findActiveMemberByPhone(String phone);

    boolean deactivateMember(Long memberId);

    void deactivateBulkMembersByEventId(Long eventId);

    Member findActiveMemberById(Long memberId);

    boolean isNickNameDuplicatedWith(String nickname, Long eventId);
  
    Member findMemberById(Long memberId);

    void save(Member activeMember);

    List<Member> findMembersByMemberIdAndEventIdAndGender(Long memberId, Long eventId, Gender gender);
}
