package com.now_here5.now_here.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    private final int corePoolSize;
    private final int maxPoolSize;
    private final int queueCapacity;
    private final String threadNamePrefix;

    public AsyncConfig(
            @Value("${async.pool.core-size}") int corePoolSize,
            @Value("${async.pool.max-size}") int maxPoolSize,
            @Value("${async.pool.queue-capacity}") int queueCapacity,
            @Value("${async.thread-name-prefix}") String threadNamePrefix) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.queueCapacity = queueCapacity;
        this.threadNamePrefix = threadNamePrefix;
    }

    @Bean(name = "asyncThreadPoolTaskExecutor")
    @Primary
    public ThreadPoolTaskExecutor asyncExecutor() {

        log.info("Async Pool - Core Pool Size: {}, Max Pool Size: {}, Queue Capacity: {}, Thread Name Prefix: {}",
                corePoolSize, maxPoolSize, queueCapacity, threadNamePrefix);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
