package com.trekker.global.config.redis.dao;

import com.trekker.global.auth.dto.RefreshTokenInfoDto;
import com.trekker.global.auth.dto.res.AuthResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final int TEMP_TOKEN_EXPIRATION = 300;
    private static final int SOCIAL_REFRESH_TOKEN_EXPIRATION = 3650;
    private static final String SOCIAL_TOKEN_REDIS_KEY = "social:refreshToken";

    /**
     * Refresh 토큰과 사용자 정보를 Redis에 저장
     */
    public void storeRefreshToken(RefreshTokenInfoDto tokenData) {
        try {
            // 기존 데이터 삭제
            redisTemplate.delete(tokenData.userAccount());

            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(tokenData.userAccount(), createTokenDataMap(tokenData));
            redisTemplate.expire(tokenData.userAccount(), 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Redis에 Refresh Token 저장 실패: {}", e.getMessage());
        }
    }

    /**
     * Refresh 토큰이 유효한지 확인
     */
    public boolean isValidRefreshToken(String userAccount, String refreshToken) {
        try {
            String storedRefreshToken = (String) redisTemplate.opsForHash()
                    .get(userAccount, "refreshToken");
            return storedRefreshToken.equals(refreshToken);
        } catch (Exception e) {
            log.warn("Redis에서 Refresh Token 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 로그아웃 시 액세스 토큰과 리프레시 토큰을 블랙리스트에 추가
     */
    public void logoutTokens(String jwtToken, long accessTokenExpiration, String userId) {
        try {
            redisTemplate.opsForValue().set(
                    jwtToken,
                    "blacklisted",
                    accessTokenExpiration,
                    TimeUnit.MILLISECONDS);
            redisTemplate.delete(userId);
        } catch (Exception e) {
            log.warn("Redis에서 로그아웃 처리 실패: {}", e.getMessage());
        }
    }

    /**
     * 사용자 권한 정보 가져오기
     */
    public String getAuthorities(String userAccount) {
        try {
            return (String) redisTemplate.opsForHash().get(userAccount, "authorities");
        } catch (Exception e) {
            log.warn("Redis에서 권한 정보 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 임시 토큰 저장
     */
    public String storeAuthResponseWithTempToken(AuthResDto authResDto) {
        String tempToken = UUID.randomUUID().toString();
        try {
            redisTemplate.opsForValue()
                    .set(tempToken, authResDto, TEMP_TOKEN_EXPIRATION, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis에 임시 토큰 저장 실패: {}", e.getMessage());
        }
        return tempToken;
    }

    /**
     * 임시 토큰 조회 및 삭제
     */
    public AuthResDto retrieveAuthResponse(String tempToken) {
        try {
            AuthResDto authResponse = (AuthResDto) redisTemplate.opsForValue().get(tempToken);
            redisTemplate.delete(tempToken);
            return authResponse;
        } catch (Exception e) {
            log.warn("Redis에서 임시 토큰 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 소셜 Refresh Token 저장
     */
    public void storeSocialRefreshTokenWithExtendedTTL(String userAccount, String refreshToken) {
        try {
            String redisKey = SOCIAL_TOKEN_REDIS_KEY + userAccount;
            redisTemplate.opsForValue()
                    .set(redisKey, refreshToken, SOCIAL_REFRESH_TOKEN_EXPIRATION, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Redis에 소셜 Refresh Token 저장 실패: {}", e.getMessage());
        }
    }

    /**
     * 소셜 Refresh Token 조회 및 삭제
     */
    public String fetchAndDeleteSocialRefreshToken(String userAccount) {
        try {
            String redisKey = SOCIAL_TOKEN_REDIS_KEY + userAccount;
            String refreshToken = (String) redisTemplate.opsForValue().get(redisKey);
            redisTemplate.delete(redisKey);
            return refreshToken;
        } catch (Exception e) {
            log.warn("Redis에서 소셜 Refresh Token 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    private HashMap<String, Object> createTokenDataMap(RefreshTokenInfoDto tokenData) {
        HashMap<String, Object> tokenDataMap = new HashMap<>();
        tokenDataMap.put("refreshToken", tokenData.refreshToken());
        tokenDataMap.put("authorities", tokenData.authorities());
        return tokenDataMap;
    }
}
