package com.now_here5.now_here.domain.matching.repository;

import com.now_here5.now_here.domain.matching.dto.MatchingWithNicknameResponse;
import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.Status;
import com.now_here5.now_here.domain.member.entity.Member;

import java.util.List;

public interface MatchingRepository {
    List<Object[]> findMemberForBanner(Status status);
    void save(Matching matching);
    Matching findById(Long matchingId);
    void update(Matching matching);
    void delete(Long matchingId);

    // memberId가 receiver인 매칭의 수를 찾음
    Long countByReceiverId(Long memberId);
    // memberId가 sender인 매칭의 수를 찾음
    Long countBySenderId(Long memberId);

    Matching findBySenderAndReceiver(Member sender, Member receiver);

    boolean existsByMembers(Member member1, Member member2);

    List<Matching> findByReceiverId(Long memberId);

    List<Matching> findBySenderId(Long memberId);

    List<MatchingWithNicknameResponse> findMatchingWithNickname(Long memberId);

    List<Matching> findAcceptedMatchingsBySenderOrReceiver(Long memberId);

    List<Matching> findMatchingsFromYesterday();
}
