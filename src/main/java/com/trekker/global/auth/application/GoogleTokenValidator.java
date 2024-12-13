package com.trekker.global.auth.application;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleTokenValidator {

    private static final String GOOGLE_TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_KEY;

    private final RestTemplate restTemplate;

    public boolean validateAccessToken(String accessToken) {
        try {
            String url = GOOGLE_TOKEN_INFO_URL + "?access_token=" + accessToken;

            // Google의 토큰 정보 가져오기
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            // 토큰 검증 로직 추가 (예: audience, expires_in 확인)
            Map<String, Object> tokenInfo = response.getBody();
            if (tokenInfo != null && GOOGLE_CLIENT_KEY.equals(tokenInfo.get("aud"))) {
                return true; // 토큰이 유효하고 클라이언트 ID와 일치함
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
