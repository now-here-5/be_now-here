package com.now_here5.now_here.domain.matching.repository;

import com.now_here5.now_here.domain.matching.entity.Matching;
import com.now_here5.now_here.domain.matching.entity.Status;

import java.util.List;

public interface MatchingRepository {
    List<Object[]> findMemberForBanner(Status status);
    void save(Matching matching);
    Matching findById(Long matchingId);
    void update(Matching matching);
    void delete(Long matchingId);
}
