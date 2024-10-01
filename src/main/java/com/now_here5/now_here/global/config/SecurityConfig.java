package com.now_here5.now_here.global.config;
import com.now_here5.now_here.global.security.exception.CustomAccessDeniedHandler;
import com.now_here5.now_here.global.security.exception.CustomAuthenticationEntryPoint;
import com.now_here5.now_here.global.security.filter.CustomAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthFilter customAuthFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 프로필에 따라 다른 CORS 설정을 사용
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers("/notification/send").hasRole("ADMIN")
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").anonymous()
                        .requestMatchers(HttpMethod.GET, "**/*.css", "**/*.png", "**/*.html").anonymous()
                        .requestMatchers("/slack/**").anonymous()
                        .requestMatchers("/dev/login").anonymous()
                        .requestMatchers(HttpMethod.GET, "/event/list").anonymous()
                        .requestMatchers(HttpMethod.POST, "*/inquiry").permitAll()
                        .requestMatchers(HttpMethod.GET, "*/verify/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "*/verify/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "*/register/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "*/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .anonymous(anonymousConfigurer -> anonymousConfigurer.authorities("ANONYMOUS"))
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(customAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
