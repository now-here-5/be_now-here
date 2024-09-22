package com.now_here5.now_here.global.logging.system;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServerResourceLogging {

    public void logSystemResources() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();

        log.info("Memory usage: Total: {}, Free: {}, Max: {}", totalMemory, freeMemory, maxMemory);
    }
}
