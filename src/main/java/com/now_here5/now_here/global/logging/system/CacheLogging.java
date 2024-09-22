package com.now_here5.now_here.global.logging.system;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheLogging {

    private final CacheManager cacheManager;

    public void logCacheStats() {
        // CacheManager에서 Spring의 Cache 객체를 가져옴
        org.springframework.cache.Cache springCache = cacheManager.getCache("bannerListCache");

        if (springCache != null) {
            Object nativeCache = springCache.getNativeCache();
            if (nativeCache instanceof Cache) {
                // Caffeine 캐시에 대한 통계 정보 가져오기
                Cache<Object, Object> caffeineCache = (Cache<Object, Object>) nativeCache;
                CacheStats stats = caffeineCache.stats();

                log.info("Cache Stats - Hit Rate: {}, Miss Rate: {}, Average Load Time: {} ms",
                        stats.hitRate(),
                        stats.missRate(),
                        stats.averageLoadPenalty());
            } else {
                log.warn("Native cache is not of expected type: Caffeine Cache");
            }
        } else {
            log.warn("Cache not found: bannerListCache");
        }
    }
}
