package com.now_here5.now_here.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        log.info("Caffeine Cache Manager initialized");
        return new CaffeineCacheManager("bannerListCache");
    }
}
