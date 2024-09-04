package com.now_here5.now_here.domain.member.service;


import com.now_here5.now_here.domain.member.dto.MemberRecommendResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.member.dto.PersonalInfoResponse;
import com.now_here5.now_here.domain.member.dto.ProfileResponse;
import com.now_here5.now_here.domain.member.dto.RegisterMemberRequest;
import java.util.List;

public interface MemberService {

    boolean sendCode(String phone);

    boolean verifyCode(String phone, String code);

    String registerMember(Long eventId, RegisterMemberRequest registerMemberRequest);

    boolean deactivateMember();

    boolean checkPhoneDuplicated(Long eventId, String phone);

    boolean checkNicknameDuplicated(Long eventId, String nickname);

    List<MemberRecommendResponse> recommendMembers();

    EventListResponse getAssignedEventsByMember();

    boolean updateDescription(String description);

    boolean updateNickName(String nickName);

    boolean updateMbti(String mbti);

    ProfileResponse getProfile();

    PersonalInfoResponse getPersonalInfo();

}
