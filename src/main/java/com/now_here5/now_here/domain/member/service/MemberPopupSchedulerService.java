package com.now_here5.now_here.domain.member.service;


import com.now_here5.now_here.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberPopupSchedulerService {
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void initializePopupByValue() {
        log.info("initializePopupByValue");
        memberRepository.initializePopupValue();
    }
}
