package com.now_here5.now_here.domain.member.service;


import com.now_here5.now_here.domain.event.converter.EventListToDto;
import com.now_here5.now_here.domain.event.dto.EventListResponse;
import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.event.repository.EventRepository;
import com.now_here5.now_here.domain.member.converter.RegisterDtoToMember;
import com.now_here5.now_here.domain.member.dto.MemberRecommendResponse;
import com.now_here5.now_here.domain.member.dto.PersonalInfoResponse;
import com.now_here5.now_here.domain.member.dto.ProfileResponse;
import com.now_here5.now_here.domain.member.dto.RegisterMemberRequest;
import com.now_here5.now_here.domain.member.entity.*;
import com.now_here5.now_here.domain.member.repository.MemberAuthRepository;
import com.now_here5.now_here.domain.member.repository.MemberRepository;
import com.now_here5.now_here.global.security.dto.AuthenticatedMemberDto;
import com.now_here5.now_here.global.util.AuthUtil;
import com.now_here5.now_here.infra.phone.service.PhoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PhoneService phoneService;
    private final RegisterDtoToMember registerDtoToMember;
    private final EventRepository eventRepository;
    private final AuthUtil authUtil;
    private final MemberAuthRepository memberAuthRepository;
    private final EventListToDto eventListToDto;


    @Override
    public boolean sendCode(String phone) {
        try{
            return  phoneService.sendVerificationCode(phone);
        } catch (Exception e) {
            log.error("Failed to send verification code to phone number: {}", phone);
            return false;
        }
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        return phoneService.verifyCode(phone, code);
    }

    @Transactional
    @Override
    public String registerMember(Long eventId, RegisterMemberRequest registerMemberRequest) {

        try{
            if(!phoneService.isVerifiedPhone(registerMemberRequest.getPhone())){
                log.debug("Phone number {} is not verified", registerMemberRequest.getPhone());
                throw new Exception("Phone number is not verified");
            }

            Member member = registerDtoToMember.converter(registerMemberRequest);
            Event event  =  eventRepository.getEventDetail(eventId);

            member.setEvent(event);
            memberRepository.save(member);
            return member.getToken();

        }catch(Exception e){
            log.error("Failed to register member: {}", e.getMessage());
            return null;
        }

    }


    @Transactional
    @Override
    public boolean deactivateMember() {
        try{
            AuthenticatedMemberDto memberDto =  authUtil.getMemberByAuthentication();
            return memberRepository.inactiveMember(memberDto.getMemberId());
        }catch(Exception e){
            log.error("Failed to inactivate member: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean checkPhoneDuplicated(Long eventId, String phone) {
        try{
            List<Member> members =  memberRepository.findActiveMemberByPhone(phone);
            log.trace("phone number {} : ",members);
            for(Member member : members){
                if(member.getEvent().getId().equals(eventId)){

                    log.debug("Phone number {} is duplicated in event {} : {}",
                            phone, eventId, member.getEvent().getField());
                    return true;
                }
            }
            log.trace("phone number {} : ",members);
                return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public boolean checkNicknameDuplicated(Long eventId, String nickname) {
        return memberRepository.isNickNameDuplicatedWith(nickname, eventId);
    }

    @Override
    public List<MemberRecommendResponse> recommendMembers() {
        AuthenticatedMemberDto authMember = authUtil.getMemberByAuthentication();
        Member member = memberRepository.findMemberById(authMember.getMemberId());
        Long eventId = authMember.getEvent().getEventId();
        Gender gender = member.getGender();

        try {
            List<Member> members = memberRepository.findMembersByEventIdAndGender(eventId, gender);
            return members.stream()
                    .map(m -> new MemberRecommendResponse(
                            m.getId(),
                            m.getMbti().toString(),
                            m.getNickname(),
                            m.getBirthday().toString(),
                            m.getGender().toString()))
                    .collect(Collectors.toUnmodifiableList());
        } catch (Exception e) {
            log.error("멤버 추천 실패: {}", e.getMessage());
            return List.of();
        }
    }
          
    public EventListResponse getAssignedEventsByMember() {
        try{
            List<Event> events =  eventRepository.getSignedEventsByMember(true,
                    authUtil.getMemberByAuthentication().getMemberId());
            return eventListToDto.converter(events);
        }catch (Exception e){
            log.error("Failed to get assigned events by member: {}", e.getMessage());
            return null;
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
    public boolean updateNotification(boolean notification) {
        try {
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            member.updateNotification(notification);

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
            Long eventId = memberDto.getEvent().getEventId();

            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            member.updateNickName(nickName);

            return true;
        } catch (Exception e) {
            log.error("Failed to update nickname: {}", e.getMessage());
            return false;
        }
    }


    public ProfileResponse getProfile() {
        try {
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());

            return new ProfileResponse(
                    member.getId(),
                    member.getMbti().toString(),
                    member.getNickname(),
                    member.getBirthday().toString(),
                    member.getGender().toString());

        } catch (Exception e) {
            log.error("Failed to get profile: {}", e.getMessage());
            return null;
        }
    }

    public PersonalInfoResponse getPersonalInfo() {
        try {
            AuthenticatedMemberDto memberDto = authUtil.getMemberByAuthentication();
            Member member = memberRepository.findMemberById(memberDto.getMemberId());
            return new PersonalInfoResponse(
                    member.getId(),
                    member.getMbti().toString(),
                    member.getNickname(),
                    member.getBirthday().toString(),
                    member.getGender().toString(),
                    member.getPhoneNumber(),
                    member.getDescription());

        } catch (Exception e) {
            log.error("Failed to get personal info: {}", e.getMessage());
            return null;
        }
    }


}
