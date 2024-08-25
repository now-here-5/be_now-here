package com.now_here5.now_here.global.config;

import com.now_here5.now_here.global.security.converter.ListRolesToDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {
    @Bean
    public ListRolesToDto listRolesToDto()  { return new ListRolesToDto();  }
}
