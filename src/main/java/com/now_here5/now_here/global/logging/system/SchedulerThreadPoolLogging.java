package com.now_here5.now_here.global.logging.system;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulerThreadPoolLogging {

    private final ThreadPoolTaskScheduler scheduler;

    public void logSchedulerStatus() {
        log.info("Scheduler Pool - Active Threads: {}, Pool Size: {}",
                scheduler.getActiveCount(),
                scheduler.getPoolSize());
    }
}
