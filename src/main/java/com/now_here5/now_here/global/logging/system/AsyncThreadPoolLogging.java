package com.now_here5.now_here.global.logging.system;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;


@Component
@Slf4j
@RequiredArgsConstructor
public class AsyncThreadPoolLogging {

    @Qualifier("asyncThreadPoolTaskExecutor")
    private final ThreadPoolTaskExecutor asyncExecutor;

    public void logAsyncExecutorStatus() {
        log.info("Async Pool - Active Threads: {}, Pool Size: {}, Queue Size: {}",
                asyncExecutor.getActiveCount(),
                asyncExecutor.getCorePoolSize(),
                asyncExecutor.getMaxPoolSize());
    }
}

