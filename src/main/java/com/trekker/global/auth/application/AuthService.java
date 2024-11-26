package com.trekker.global.auth.application;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.entity.Member;
import com.trekker.global.auth.dto.res.AuthResDto;
import com.trekker.global.auth.dto.res.RefreshTokenResDto;
import com.trekker.global.config.redis.dao.RedisRepository;
import com.trekker.global.config.security.TokenProvider;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RedisRepository redisRepository;
    private final UnlinkService unlinkService;


    @Transactional
    public void logout(String jwtToken) {
        long accessTokenExpiration = tokenProvider.getExpiration(jwtToken);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);

        redisRepository.logoutTokens(jwtToken, accessTokenExpiration, userId);
    }

    /**
     * Refresh 토큰을 이용한 Access 토큰 갱신
     *
     * @param refreshToken 클라이언트로부터 전달받은 Refresh 토큰
     * @return AuthResponseDto 새로 생성된 Access 토큰과 기존의 Refresh 토큰
     */
    public AuthResDto refreshAccessToken(String refreshToken) {
        // JwtTokenProvider 에서 Refresh 토큰 유효성 검사 및 Access 토큰 재발급
        try {
            return tokenProvider.refreshAccessToken(refreshToken);
        } catch (BusinessException e) {
            throw new BusinessException(refreshToken, "refreshToken",
                    ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    /**
     * Refresh 토큰 만료시 Refresh 토큰 갱신
     *
     * @param refreshToken 클라이언트로부터 전달받은 기존 Refresh 토큰
     * @return RefreshTokenResDto 새로 생성된 Refresh 토큰
     */
    public RefreshTokenResDto reissueRefreshToken(String refreshToken) {
        // JwtTokenProvider 에서 Refresh 토큰 유효성 검사 및 Access 토큰 재발급
        try {
            return tokenProvider.reissueRefreshToken(refreshToken);
        } catch (BusinessException e) {
            throw new BusinessException(refreshToken, "refreshToken",
                    ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    /**
     * 임시 토큰을 사용하여 Redis에서 인증 응답 데이터를 조회합니다.
     *
     * @param tempToken 임시 토큰 (프론트엔드에서 받은 tempToken)
     * @return AuthResponseDto 인증에 필요한 Access, Refresh 토큰 조회된 데이터가 없을 경우 null을 반환합니다.
     */
    public AuthResDto retrieveAuthResponse(String tempToken) {
        return redisRepository.retrieveAuthResponse(tempToken);
    }

    /**
     * 회원 계정으로 회원을 조회하고, 삭제합니다. 삭제하면서 연결된 소셜 계정과 연결을 끊습니다.
     *
     * @param email 사용자 계정
     */
    @Transactional
    public void deleteAccount(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(
                        () -> new BusinessException(email, "email", ErrorCode.MEMBER_NOT_FOUND));

        // 소셜 언링크 (연결 끊기)
        unlinkService.unlink(member);

        // 회원 삭제
        member.markAsDeleted();
    }
}
