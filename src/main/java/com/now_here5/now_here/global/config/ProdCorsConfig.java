package com.now_here5.now_here.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@Profile("prod")
@Slf4j
public class ProdCorsConfig {
    @Primary
    @Bean(name = "prodCorsConfigurationSource")
    public CorsConfigurationSource corsConfigurationSource(){
        log.info("Prod Cors config will be injected.");

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("https://www.now-here.site");
        configuration.addAllowedOrigin("https://api.now-here.site");

        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");

        // 특정 헤더만 허용
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedHeader("Content-Type");
        configuration.addAllowedHeader("Accept");

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

