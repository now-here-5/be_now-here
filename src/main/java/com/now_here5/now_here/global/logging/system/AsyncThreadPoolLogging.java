package com.now_here5.now_here.global.logging.system;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class AsyncThreadPoolLogging {

    @Qualifier("asyncExecutor")
    private final Executor asyncExecutor;

    public void logAsyncExecutorStatus() {
        if (asyncExecutor instanceof ThreadPoolTaskExecutor) {
            ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) asyncExecutor;
            log.info("Async Pool - Active Threads: {}, Pool Size: {}, Queue Size: {}",
                    taskExecutor.getActiveCount(),
                    taskExecutor.getPoolSize(),
                    taskExecutor.getThreadPoolExecutor().getQueue().size());
        } else {
            log.warn("The provided Executor is not a ThreadPoolTaskExecutor.");
        }
    }
}
