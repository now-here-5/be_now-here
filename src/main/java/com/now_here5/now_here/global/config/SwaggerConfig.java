package com.now_here5.now_here.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SwaggerConfig {

    @Bean
    @Profile("dev")
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NowHere API")
                        .version("1.0")
                        .description("API Documentation"))
                // HTTPS 서버 추가
                .addServersItem(new Server().url("https://api.now-here.site").description("Production server"))
                .addServersItem(new Server().url("https://port-0-now-here-server-754g42aluutr2qo.sel5.cloudtype.app/").description("Test server"))
                .addServersItem(new Server().url("http://localhost:8080").description("Local server"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")));
    }
}
