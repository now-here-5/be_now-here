package com.now_here5.now_here.global.logging.system;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AsyncThreadPoolLogging {

    private final ThreadPoolTaskExecutor taskExecutor;

    public void logAsyncExecutorStatus() {
        log.info("Async Pool - Active Threads: {}, Pool Size: {}, Queue Size: {}",
                taskExecutor.getActiveCount(),
                taskExecutor.getPoolSize(),
                taskExecutor.getThreadPoolExecutor().getQueue().size());
    }
}
