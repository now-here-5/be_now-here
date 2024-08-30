package com.now_here5.now_here.global.config;
import com.now_here5.now_here.domain.member.entity.role.RoleName;
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
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )
                // CSRF 보호 비활성화 (주로 API를 사용하는 경우)
                .csrf(AbstractHttpConfigurer::disable
                )
                // 인증 요청 처리
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // 권한 없이 접근 가능한 URL 설정

                                // 매니저 대상
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/slack/**").permitAll()
                                // 비유저 대상
                                .requestMatchers(HttpMethod.GET, "*/verify/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "*/verify/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "*/register/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "*/login/**").permitAll()

                                // 어드민 대상
                                .requestMatchers("/admin/**").permitAll()



                                // 나머지는 인증 필요.
                                .anyRequest().authenticated()

                ).exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )

                // 세션 사용 안 함 (Stateless)
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 필터에 커스텀 인증 필터 추가 : 무작위 문자열 방식
                .addFilterBefore(customAuthFilter, UsernamePasswordAuthenticationFilter.class) ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 방식 설정
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(clientOriginUrl); // 허용할 클라이언트 도메인 설정
        configuration.addAllowedMethod("GET"); // 허용할 메서드 설정
        configuration.addAllowedMethod("POST");
        configuration.setAllowCredentials(true); // 자격 증명 허용 (예: 쿠키 등)
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

