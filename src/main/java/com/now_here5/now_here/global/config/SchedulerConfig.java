package com.now_here5.now_here.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    @Value("${scheduling.thread.pool.size}")
    private int poolSize;

    @Bean(name = "taskScheduler")
    public TaskScheduler taskScheduler() {
        log.info("Scheduling Pool - Pool Size: {}", poolSize);
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);  // 스레드 풀 크기 설정
        scheduler.setThreadNamePrefix("EventScheduler-");  // 스레드 이름 접두어 설정
        scheduler.initialize();
        return scheduler;
    }
}