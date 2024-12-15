package com.trekker.global.config.security;

import com.trekker.global.auth.custom.CustomUserDetailsService;
import com.trekker.global.config.redis.dao.RedisRepository;
import com.trekker.global.config.security.filter.JwtFilter;
import com.trekker.global.config.security.handler.CustomAccessDeniedHandler;
import com.trekker.global.config.security.handler.CustomAuthenticationEntryPoint;
import com.trekker.global.config.security.handler.CustomLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final RedisRepository redisRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomLoginSuccessHandler loginSuccessHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        STATELESS)) // 세션 정책: STATELESS
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers(
                                        // 인증 없이 접근 가능한 엔드포인트
                                        "/api/v1/auth/access/refresh",
                                        "/api/v1/auth/refresh/reissue",
                                        "/api/v1/auth/issue-final-token",
                                        "/uploads/profile-images/**",
                                        "/login/oauth2/**",
                                        "/oauth2/**",
                                        "/error",
                                        "/favicon.ico",
                                        "/v3/api-docs/**",       // Swagger JSON 문서
                                        "/swagger-ui/**",        // Swagger UI 리소스
                                        "/swagger-ui.html"       // Swagger UI 진입점
                                ).permitAll()
                                .anyRequest().authenticated()) // 그 외 요청은 인증 필요
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(
                                userInfo -> userInfo.userService(customUserDetailsService))
                        .successHandler(loginSuccessHandler))
                .addFilterBefore(new JwtFilter(tokenProvider, redisRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 허용된 Origin
        configuration.setAllowedMethods(
                Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH")); // 허용된 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키를 포함한 요청 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
        return source;
    }
}
