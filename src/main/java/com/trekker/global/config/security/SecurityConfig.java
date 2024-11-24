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
                .httpBasic(
                        AbstractHttpConfigurer::disable) // HTTP Basic 인증을 비활성화 (JWT 방식을 사용하기 위해서)
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호를 비활성화 (JWT 인증은 세션이 아닌 토큰 기반이라 필요 없음)
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource())) // CORS 설정을 활성화하여 corsConfigurationSource()에서 상세 설정을 정의
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        STATELESS)) // 세션 정책을 STATELESS로 설정하여 서버에서 세션을 생성하지 않음
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/api/v1/auth/refresh",
                                        "/api/v1/auth/issue-final-token"
                                        , "/login/oauth2/**", "/oauth2/**", "/error",
                                        "/favicon.ico").permitAll()
                                .anyRequest().authenticated())
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
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(
                Arrays.asList("HEAD", "GET", "POST", "DELETE", "PATCH")); // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 쿠키를 포함한 요청 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위의 CORS 설정 적용
        return source;
    }
}
