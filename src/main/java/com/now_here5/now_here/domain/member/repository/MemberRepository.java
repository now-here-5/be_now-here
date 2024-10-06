package com.now_here5.now_here.domain.member.repository;


import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.Member;
import java.util.List;

public interface MemberRepository {
    boolean doesMemberExistByPhoneNumber(String phoneNumber, Long eventId);

    boolean deactivateMember(Long memberId);

    void deactivateBulkMembersByEventId(Long eventId);

    Member findActiveMemberById(Long memberId);

    boolean isNickNameDuplicatedInEvent(String nickname, Long eventId);

    boolean isPhoneDuplicated(String phone, Long eventId);

    Member findMemberById(Long memberId);

    void save(Member activeMember);

    void initializePopupValue();

    List<Member> findMembersByMemberIdAndEventIdAndGender(Long memberId, Long eventId, Gender gender);

    int getSpecialHeartCountByMemberId(Long memberId);

    void updateSpecialHeart(Long memberId, int specialHeartCount);

}
