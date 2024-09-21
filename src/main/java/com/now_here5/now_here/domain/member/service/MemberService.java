package com.now_here5.now_here.domain.member.service;


import com.now_here5.now_here.domain.member.dto.MemberRecommendResponse;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.member.dto.PersonalInfoResponse;
import com.now_here5.now_here.domain.member.dto.ProfileResponse;
import com.now_here5.now_here.domain.member.dto.RegisterMemberRequest;

import java.time.LocalDate;
import java.util.List;

public interface MemberService {

    boolean sendCode(String phoneNumber);

    boolean verifyCode(String phone, String code);

    String registerMember(Long eventId, RegisterMemberRequest registerMemberRequest);

    boolean deactivateMember();

    boolean checkNicknameDuplicated(Long eventId, String nickname);

    boolean checkIfPhoneDuplicated(Long eventId, String phone);

    List<MemberRecommendResponse> recommendMembers();

    EventListResponse getAssignedEventsByMember();

    boolean getNotificationSetting();

    boolean updateDescription(String description);

    boolean updateBirthday(LocalDate birthday);

    boolean updateNotificationSetting(boolean notification);

    boolean updateNickName(String nickName);

    boolean updateMbti(String mbti);

    ProfileResponse getProfile();

    PersonalInfoResponse getPersonalInfo();

    void offerSpecialHeartIfQualified();
}
