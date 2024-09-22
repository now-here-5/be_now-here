package com.now_here5.now_here.global.logging.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SystemResourceLoggingScheduler {

    private final AsyncThreadPoolLogging asyncThreadPoolLogging;
    private final CacheLogging cacheLogging;
    private final DatabaseThreadPoolLogging databaseThreadPoolLogging;
    private final ServerResourceLogging serverResourceLogging;
    private final SchedulerThreadPoolLogging schedulerThreadPoolLogging;
    private final MemoryCacheLogging memoryCacheLogging;

    // 통합된 로깅 메서드
    @Scheduled(fixedRate = 60000) // 매 60초마다 모든 로깅 호출
    public void logAllSystems() {
        log.debug("Starting unified logging...");
        asyncThreadPoolLogging.logAsyncExecutorStatus();
        cacheLogging.logCacheStats();
        databaseThreadPoolLogging.logHikariCPStatus();
        serverResourceLogging.logSystemResources();
        schedulerThreadPoolLogging.logSchedulerStatus();
        memoryCacheLogging.logMemoryCacheStats();
        log.debug("Unified logging completed.");
    }
}
