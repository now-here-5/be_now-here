package com.now_here5.now_here.domain.member.service;


import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.event.repository.EventRepository;
import com.now_here5.now_here.domain.member.converter.RegisterDtoToMember;
import com.now_here5.now_here.domain.member.dto.MemberRecommendResponse;
import com.now_here5.now_here.domain.member.dto.RegisterMemberRequest;
import com.now_here5.now_here.domain.member.entity.*;
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
            memberRepository.add(member);
            return member.getToken();

        }catch(Exception e){
            log.error("Failed to register member: {}", e.getMessage());
            return null;
        }

    }


    @Transactional
    @Override
    public boolean inactivateMember() {
        try{
            AuthenticatedMemberDto memberDto =  authUtil.getMemberByAuthentication();
            Member activeMember = memberRepository.findActiveMemberById(memberDto.getMemberId());
            return true; // 추후 개발
        }catch(Exception e){
            log.error("Failed to inactivate member: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean checkPhoneDuplicated(Long eventId, String phone) {
        try{
            List<Member> members =  memberRepository.findActiveMemberByPhone(phone);

            for(Member member : members){
                if(member.getEvent().getId().equals(eventId)){
                    log.debug("Phone number {} is duplicated in event {} : {}",
                            phone, eventId, member.getEvent().getField());
                    return true;
                }
            }
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
}
