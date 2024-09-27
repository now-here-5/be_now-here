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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${client.origin.url}")
    private String clientOriginUrl;

    private final CustomAuthFilter customAuthFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF 보호 비활성화 (주로 API를 사용하는 경우)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // 관리자 권한이 필요한 URL
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").anonymous() // 개발 시.

                        // 익명 사용자만 접근 가능한 URL
                        .requestMatchers(HttpMethod.GET, "/css/**", "/js/**").anonymous()
                        .requestMatchers("/slack/**").anonymous()
                        .requestMatchers("/dev/login").anonymous()
                        .requestMatchers(HttpMethod.GET, "/event/list").anonymous()
                        .requestMatchers(HttpMethod.POST, "*/inquiry").anonymous()
                        .requestMatchers(HttpMethod.GET, "*/verify/**").anonymous()
                        .requestMatchers(HttpMethod.POST, "*/verify/**").anonymous()
                        .requestMatchers(HttpMethod.POST, "*/register/**").anonymous()
                        .requestMatchers(HttpMethod.POST, "*/login/**").anonymous()

                        // 그 외 모든 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()
                )
                // 익명 사용자 설정
                .anonymous(anonymousConfigurer -> anonymousConfigurer
                        .authorities("ANONYMOUS")
                )
                // 예외 처리
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // 세션 사용 안 함 (Stateless)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 필터에 커스텀 인증 필터 추가
                .addFilterBefore(customAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 방식 설정
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 명시적 도메인 설정
        configuration.addAllowedOrigin("https://www.now-here.site");
        // 필요한 HTTP 메서드만 허용 (보안 강화)
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");

        // 허용할 헤더 제한 (보안 강화)
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedHeader("Content-Type");
        configuration.addAllowedHeader("Accept");

        configuration.setAllowCredentials(false);  // 자격 증명 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
