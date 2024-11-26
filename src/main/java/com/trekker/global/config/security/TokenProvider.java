package com.trekker.global.config.security;

import com.trekker.global.auth.custom.CustomUserDetails;
import com.trekker.global.auth.dto.RefreshTokenInfoDto;
import com.trekker.global.auth.dto.res.AuthResDto;
import com.trekker.global.auth.dto.res.RefreshTokenResDto;
import com.trekker.global.config.redis.dao.RedisRepository;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {

    private final Key key;
    private final RedisRepository redisRepository;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    @Autowired
    public TokenProvider(@Value("${jwt.secret}") String secretKey,
            RedisRepository redisRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisRepository = redisRepository;
    }

    /**
     * Authentication 객체로부터 Access 토큰, Refresh 토큰 생성 및 AuthResponse 반환
     */
    public AuthResDto createAuthResponse(Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = createToken(authentication.getName(), roles, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = createToken(authentication.getName(), null, REFRESH_TOKEN_EXPIRATION);

        // Redis에 Refresh 토큰 저장을 위해 RefreshTokenInfoDto 생성
        RefreshTokenInfoDto refreshTokenInfoDto = new RefreshTokenInfoDto(
                authentication.getName(),
                refreshToken,
                roles
        );
        // Redis에 Refresh 토큰 저장
        redisRepository.storeRefreshToken(refreshTokenInfoDto);

        return new AuthResDto(accessToken, refreshToken, false);
    }

    // 공통 토큰 생성 로직
    private String createToken(String userId, String roles, long expiration) {
        Claims claims = Jwts.claims().setSubject(userId);
        if (roles != null) {
            claims.put("roles", roles);
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh 토큰을 이용한 Access 토큰 갱신
     */
    public AuthResDto refreshAccessToken(String refreshToken) {
        // 1. 토큰 유효성 검사
        validateToken(refreshToken);

        // 2. 토큰에서 사용자 ID 추출
        String userId = getUserIdFromToken(refreshToken);

        // 3. Redis에서 Refresh 토큰 유효성 검사
        if (!redisRepository.isValidRefreshToken(userId, refreshToken)) {
            throw new BusinessException(refreshToken, "refreshToken",
                    ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4. Redis에서 사용자 권한 정보 추출
        String authorities = redisRepository.getAuthorities(userId);

        // 5. Refresh 토큰의 남은 만료 시간 확인
        long remainingTime = getExpiration(refreshToken);
        // 남은 시간이 전체 만료시간의 30%보다 적은 경우, 만료 임박 상태로 간주
        boolean refreshTokenWillExpire = remainingTime < (REFRESH_TOKEN_EXPIRATION * 0.3);

        // 6. 새로운 Access 토큰 생성
        String newAccessToken = createToken(userId, authorities, ACCESS_TOKEN_EXPIRATION);

        // 7. 새로운 AuthResponseDto 반환 (기존 Refresh 토큰 유지)
        return new AuthResDto(newAccessToken, refreshToken, refreshTokenWillExpire);
    }

    /**
     * Refresh 토큰 갱신 로직
     *
     * @param currentRefreshToken 기존 Refresh 토큰
     * @return 새로 생성된 Refresh 토큰
     */
    public RefreshTokenResDto reissueRefreshToken(String currentRefreshToken) {
        // 1. 기존 Refresh 토큰 유효성 검사
        validateToken(currentRefreshToken);

        // 2. 사용자 ID 추출
        String userId = getUserIdFromToken(currentRefreshToken);

        // 3. Redis에서 기존 Refresh 토큰 유효성 검사
        if (!redisRepository.isValidRefreshToken(userId, currentRefreshToken)) {
            throw new BusinessException(currentRefreshToken, "refreshToken",
                    ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4. 새로운 Refresh 토큰 생성
        String newRefreshToken = createToken(userId, null, REFRESH_TOKEN_EXPIRATION);

        // 5. Redis에 새 Refresh 토큰 저장
        RefreshTokenInfoDto refreshTokenInfoDto = new RefreshTokenInfoDto(
                userId,
                newRefreshToken,
                redisRepository.getAuthorities(userId) // 사용자 권한 정보 가져오기
        );
        redisRepository.storeRefreshToken(refreshTokenInfoDto);

        // 6. 새 Refresh 토큰 반환
        return new RefreshTokenResDto(newRefreshToken);
    }


    // 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 사용자 정보 추출 후 Authentication 객체 생성
     *
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 사용자 정보 추출
        Claims claims = parseClaims(token);
        String email = claims.getSubject();
        String roles = claims.get("roles", String.class);

        // 2. 권한 정보 설정
        Collection<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 3. CustomUserDetails 객체 생성
        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                .email(email)
                .authorities(authorities)
                .build();

        // 4. Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken(customUserDetails, token, authorities);
    }

    /**
     * 토큰에서 만료 시간 가져오기
     *
     * @param token JWT 토큰
     * @return 토큰의 남은 만료 시간 (밀리초)
     */
    public long getExpiration(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    /**
     * 토큰 검증
     *
     * @param token 검증할 토큰
     * @return 올바르면 true, 올바르지 않으면 false
     */
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다.");
            throw e; // 만료된 토큰은 그대로 던짐
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰이 잘못되었습니다.");
        }
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
