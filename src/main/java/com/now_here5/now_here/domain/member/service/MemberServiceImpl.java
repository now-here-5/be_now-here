package com.now_here5.now_here.domain.member.service;


import com.now_here5.now_here.domain.event.converter.EventListToDto;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.event.repository.EventRepository;
import com.now_here5.now_here.domain.interaction.repository.InteractionRepository;
import com.now_here5.now_here.domain.matching.service.PreferenceBasedMBTIMatching;
import com.now_here5.now_here.domain.member.converter.RegisterDtoToMember;
import com.now_here5.now_here.domain.member.dto.MemberRecommendResponse;
import com.now_here5.now_here.domain.member.dto.PersonalInfoResponse;
import com.now_here5.now_here.domain.member.dto.ProfileResponse;
import com.now_here5.now_here.domain.member.dto.RegisterMemberRequest;
import com.now_here5.now_here.domain.member.entity.*;
import com.now_here5.now_here.domain.member.entity.role.Role;
import com.now_here5.now_here.domain.member.entity.role.RoleName;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.util.AuthUtil;
import com.now_here5.now_here.infra.notification.service.PhoneCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PhoneCodeService phoneCodeService;
    private final RegisterDtoToMember registerDtoToMember;
    private final EventRepository eventRepository;
    private final AuthUtil authUtil;
    private final EventListToDto eventListToDto;
    private final PreferenceBasedMBTIMatching matcher;
    private final InteractionRepository interactionRepository;
    private final RoleAdminService roleAdminService;
    @Override
    public boolean sendCode(String phoneNumber) {
        try{
            return phoneCodeService.sendVerificationCode(phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send verification code to notification number: {}", phoneNumber);
            return false;
        }
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        return phoneCodeService.verifyCode(phone, code);
    }

    @Transactional
    @Override
    public String registerMember(Long eventId, RegisterMemberRequest registerMemberRequest) {
        log.warn("event id : {}", eventId);
        try{

            if(!phoneCodeService.isPhoneVerified(registerMemberRequest.getPhoneNumber())){
                log.error("Phone number is not verified");
                return "";
            }
            List<Role> roles = roleAdminService.findRoleByName(List.of(RoleName.USER, RoleName.ANONYMOUS));
            Member member = registerDtoToMember.converter(registerMemberRequest, roles);
            Event event  =  eventRepository.getEventDetail(eventId);

            member.setEvent(event);
            memberRepository.save(member);
            return member.getToken();

        }catch(Exception e){
            log.error("Failed to register member: {}", e.getMessage());
            return "";
        }

    }


    @Transactional
    @Override
    public boolean deactivateMember() {
        try{
            AuthenticatedMemberDto memberDto =  authUtil.getMemberByAuthentication();
            return memberRepository.deactivateMember(memberDto.getMemberId());
        }catch(Exception e){
            log.error("Failed to inactivate member: {}", e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkNicknameDuplicated(Long eventId, String nickname) {
        return memberRepository.isNickNameDuplicatedInEvent(nickname, eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkIfPhoneDuplicated(Long eventId, String phone) {
        return memberRepository.isPhoneDuplicated(phone, eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MemberRecommendResponse> recommendMembers() {
        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
        Member member = memberRepository.findMemberById(authMember.getMemberId());
        Long memberId = member.getId();
        Long eventId = authMember.getEventId();
        Gender gender = member.getGender();
        MBTI memberMbti = member.getMbti();

        try {
            // 잠재적인 매칭 후보자들을 JPQL로 가져오기
            List<Member> potentialMatches = memberRepository.findMembersByMemberIdAndEventIdAndGender(memberId, eventId, gender);

            // 가중치를 반영한 매칭 결과 계산
            List<PreferenceBasedMBTIMatching.MatchResult> matchResults = matcher.findMatches(
                    memberMbti,
                    member.getGender(),
                    potentialMatches.stream()
                            .map(PreferenceBasedMBTIMatching.PotentialMatch::new)  // Member 객체를 직접 PotentialMatch로 변환
                            .toList()
            );

            // 매칭 점수가 높은 순으로 결과 반환
            return matchResults.stream()
                    .map(result -> new MemberRecommendResponse(
                            result.getMatch().getId(),  // 추천된 멤버의 ID
                            result.getMatch().getMbti().toString(),  // MBTI
                            result.getMatch().getNickname(),  // 닉네임
                            result.getMatch().getBirthday().toString(),  // 생일
                            result.getMatch().getGender().toString(),  // 성별
                            result.getMatch().getDescription()  // 설명
                    ))
                    .toList();

        } catch (Exception e) {
            log.error("멤버 추천 실패: {}", e.getMessage());
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public EventListResponse getAssignedEventsByMember() {
        try{
            List<Event> events =  eventRepository.getSignedEventsByMember(true,
                    authUtil.getMemberByAuthentication().getMemberId());
            return eventListToDto.converter(events,false);
        }catch (Exception e){
            log.error("Failed to get assigned events by member: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean getNotificationSetting() {
        try{
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            return member.isNotiSetting();
        }catch (Exception e){
            log.error("Failed to get notification setting: {}", e.getMessage());
            throw new RuntimeException("Failed to get notification setting");
        }
    }

    @Transactional
    @Override
    public boolean updateDescription(String description) {
        try {
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            member.updateDescription(description);

            return true;
        } catch (Exception e) {
            log.error("Failed to update description: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    @Override
    public boolean updateBirthday(LocalDate birthday) {
        try {
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            member.updateBirthday(birthday);

            return true;
        } catch (Exception e) {
            log.error("Failed to update birthday: {}", e.getMessage());
            return false;
        }
    }


    @Transactional
    @Override
    public boolean updateNotificationSetting(boolean notiSetting) {
        try {
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            member.updateNotiSetting(notiSetting);

            return true;
        } catch (Exception e) {
            log.error("Failed to update notification: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    @Override
    public  boolean updateMbti(String mbti){
        try {
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            member.updateMbti(MBTI.valueOf(mbti.toUpperCase()));

            return true;
        } catch (Exception e) {
            log.error("Failed to update mbti: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    @Override
    public boolean updateNickName(String nickName) {
        try {

            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            member.updateNickName(nickName);

            return true;
        } catch (Exception e) {
            log.error("Failed to update nickname: {}", e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        try {
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());

            return new ProfileResponse(
                    member.getId(),
                    member.getMbti().toString(),
                    member.getNickname(),
                    member.getBirthday().toString(),
                    member.getGender().toString(),
                    member.getDescription());

        } catch (Exception e) {
            log.error("Failed to get profile: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public PersonalInfoResponse getPersonalInfo() {
        try {
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            return new PersonalInfoResponse(
                    member.getId(),
                    member.getPhoneNumber(),
                    member.getMbti().toString(),
                    member.getNickname(),
                    member.getGender().toString(),
                    member.getBirthday().toString(),
                    member.getDescription()
                    );

        } catch (Exception e) {
            log.error("Failed to get personal info: {}", e.getMessage());
            return null;
        }
    }


    @Override
    @Transactional
    public void offerSpecialHeartIfQualified(Long memberId, int specialHeartCount) {
        if(interactionRepository.isFeedbackFirstWritten(memberId)){
            log.info("Offer special heart to member: {}", memberId);
            memberRepository.updateSpecialHeart(memberId, specialHeartCount);
        }
    }
}
