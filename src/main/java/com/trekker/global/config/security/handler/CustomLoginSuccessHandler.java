package com.trekker.global.config.security.handler;

import com.trekker.global.auth.custom.CustomUserDetails;
import com.trekker.global.auth.dto.res.AuthResDto;
import com.trekker.global.config.redis.dao.RedisRepository;
import com.trekker.global.config.security.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RedisRepository redisRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private static final String TEMP_TOKEN_NAME = "tempToken";
    private static final String USER_ACCOUNT = "account";
    private static final String IS_COMPLETED = "isCompleted";

    @Value("${login.success-url}")
    private String SUCCESS_URL;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        CustomUserDetails oAuth2User = (CustomUserDetails) authentication.getPrincipal();
//        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        // 최종 액세스 및 리프레시 토큰 생성
        AuthResDto authResponse = tokenProvider.createAuthResponse(authentication);

        // Redis에 authResponse 저장 및 임시 토큰 발급
        String tempToken = redisRepository.storeAuthResponseWithTempToken(authResponse);
        String account = oAuth2User.getId();

        // 제공자가 google일 경우, 추후 회원 탈퇴를 위한 리프래시 토큰 저장
//        handleGoogleRefreshToken(authToken, account);

        // 성공 URL에 쿼리 파라미터로 임시 토큰과 isGuest 정보를 전달
        String redirectUrl = UriComponentsBuilder.fromUriString(SUCCESS_URL)
                .queryParam(TEMP_TOKEN_NAME, tempToken)
                .queryParam(USER_ACCOUNT, account)
                .queryParam(IS_COMPLETED, oAuth2User.getIsCompleted())
                .build().toUriString();

        response.sendRedirect(redirectUrl); // 프론트엔드로 리다이렉트
    }

    /**
     * Google 소셜 Refresh Token 저장 처리
     */
    private void handleGoogleRefreshToken(OAuth2AuthenticationToken authToken, String account) {
        if (authToken.getAuthorizedClientRegistrationId().equals("google")) {
            String socialRefreshToken = fetchGoogleRefreshToken(authToken);
            if (socialRefreshToken != null) {
                redisRepository.storeSocialRefreshTokenWithExtendedTTL(account, socialRefreshToken);
                log.info("Google Refresh Token 저장 완료: {}", socialRefreshToken);
            } else {
                log.warn("Google Refresh Token을 가져오지 못했습니다. account={}", account);
            }
        }
    }

    /**
     * Google Refresh Token을 가져오는 메서드
     */
    private String fetchGoogleRefreshToken(OAuth2AuthenticationToken authToken) {
        // Spring Security에서 OAuth2AuthorizedClient 가져오기
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authToken.getAuthorizedClientRegistrationId(),
                authToken.getName());

        if (client != null && client.getRefreshToken() != null) {
            return client.getRefreshToken().getTokenValue();
        }
        return null;
    }
}
