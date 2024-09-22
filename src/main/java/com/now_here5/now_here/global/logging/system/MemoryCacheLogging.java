package com.now_here5.now_here.global.logging.system;

import com.now_here5.now_here.infra.notification.repository.MemoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MemoryCacheLogging {

    private final MemoryRepository memoryRepository;
    private final int codeMaxMemorySize;
    private final int codeStandardMemorySize;

    public MemoryCacheLogging(MemoryRepository memoryRepository,
                        @Value("${checkCodeMap.codeMaxMemorySize}") int codeMaxMemorySize,
                        @Value("${checkCodeMap.codeStandardMemorySize}") int codeStandardMemorySize) {
        this.memoryRepository = memoryRepository;
        this.codeMaxMemorySize = codeMaxMemorySize;
        this.codeStandardMemorySize = codeStandardMemorySize;
    }

    public void logMemoryCacheStats() {
        int totalMemory = memoryRepository.getCheckCodeMapSize();
        log.info("Code Memory Stats - Current: {} MB, Standard: {} MB, Max: {} MB",
                totalMemory, codeStandardMemorySize, codeMaxMemorySize);
    }
}
