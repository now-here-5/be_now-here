package com.now_here5.now_here.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition
public class SwaggerConfig{
    @Value("${swagger.enabled}")
    private boolean enabled;

    @Value("${swagger.url}")
    private String url;

    @Bean
    @ConditionalOnProperty(name = "swagger.enabled", havingValue = "true", matchIfMissing = true)
    public OpenAPI customOpenAPI() {
        if(!enabled){
            return null; // swagger is disabled.
        }
        return new OpenAPI()
                .info(new Info()
                        .title("NowHere API")
                        .title("API Documentation")
                        .version("1.0")
                        .description("API Documentation for the application"))
                .addServersItem(new Server().url(url));
    }
}