package com.trekker.global.auth.application;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.dao.MemberWithdrawalFeedbackRepository;
import com.trekker.domain.member.dto.req.MemberWithdrawalReqDto;
import com.trekker.domain.member.entity.Member;
import com.trekker.global.auth.custom.CustomUserDetails;
import com.trekker.global.auth.custom.CustomUserDetailsService;
import com.trekker.global.auth.dto.req.GoogleLoginReqDto;
import com.trekker.global.auth.dto.res.AuthResDto;
import com.trekker.global.auth.dto.res.GoogleRes;
import com.trekker.global.auth.dto.res.RefreshTokenResDto;
import com.trekker.global.config.redis.dao.RedisRepository;
import com.trekker.global.config.security.TokenProvider;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberWithdrawalFeedbackRepository feedbackRepository;
    private final TokenProvider tokenProvider;
    private final RedisRepository redisRepository;
    private final UnlinkService unlinkService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final GoogleTokenValidator googleTokenValidator;


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
     * @param memberId 사용자 memberId
     * @param reqDto
     */
    @Transactional
    public void deleteAccount(Long memberId, MemberWithdrawalReqDto reqDto) {
        Member member = memberRepository.findByIdWithSocialAndOnboarding(memberId)
                .orElseThrow(
                        () -> new BusinessException(memberId, "memberId", ErrorCode.MEMBER_NOT_FOUND));

        // 탈퇴 이유 및 피드백 저장
        feedbackRepository.save(reqDto.toEntity(member));

        // 소셜 언링크 (연결 끊기)
        unlinkService.unlink(member, reqDto);

        // 회원 삭제
        member.markAsDeleted();
    }

    /**
     * 구글 로그인을 직접 수행합니다.
     * @param request 구글 사용자 정보 및 엑세스 토큰을 담고있는 dto
     * @return 자체 발급한 토큰과 온보딩 완료 여부를 포함한 dto
     */
    @Transactional
    public GoogleRes authenticateGoogleUser(GoogleLoginReqDto request) {
        // 1. Google Access Token 검증
        validateGoogleAccessToken(request.accessToken());

        // 2. OAuth2UserRequest 생성
        OAuth2UserRequest userRequest = createOAuth2UserRequest(request.accessToken());

        // 3. 사용자 정보 로드 및 Authentication 생성
        Authentication authentication = createAuthentication(userRequest);

        // 4. SecurityContext에 Authentication 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. JWT 생성 및 최종 응답 반환
        return createGoogleResponse(authentication);
    }

    /**
     * Google Access Token의 유효성을 검증합니다.
     *
     * @param accessToken Google Access Token
     * @throws IllegalArgumentException 유효하지 않은 토큰일 경우 예외를 발생시킵니다.
     */
    private void validateGoogleAccessToken(String accessToken) {
        boolean googleTokenValid = googleTokenValidator.validateAccessToken(accessToken);
        if (!googleTokenValid) {
            throw new IllegalArgumentException("유효하지 않은 Google Access Token입니다.");
        }
    }

    /**
     * Google Access Token을 기반으로 OAuth2UserRequest를 생성합니다.
     *
     * @param accessToken Google Access Token
     * @return OAuth2UserRequest OAuth2 사용자 요청 객체
     * @throws IllegalStateException Google 클라이언트 설정을 찾을 수 없을 경우 예외를 발생시킵니다.
     */
    private OAuth2UserRequest createOAuth2UserRequest(String accessToken) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("google");
        if (registration == null) {
            throw new IllegalStateException("Google 클라이언트 설정을 찾을 수 없습니다.");
        }

        OAuth2AccessToken token = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                accessToken,
                null,
                null
        );

        return new OAuth2UserRequest(registration, token);
    }

    /**
     * OAuth2UserRequest를 기반으로 사용자 정보를 로드하고 Authentication 객체를 생성합니다.
     *
     * @param userRequest OAuth2 사용자 요청 객체
     * @return Authentication 인증 객체
     */
    private Authentication createAuthentication(OAuth2UserRequest userRequest) {
        // CustomUserDetailsService를 이용하여 OAuth2 사용자 정보를 로드합니다.
        OAuth2User oAuth2User = customUserDetailsService.loadUser(userRequest);

        // 로드된 사용자 정보를 CustomUserDetails로 변환합니다.
        CustomUserDetails userDetails = (CustomUserDetails) oAuth2User;

        // Authentication 객체를 생성하여 반환합니다.
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    /**
     * Authentication 객체를 기반으로 JWT를 생성하고 최종 응답 객체를 생성합니다.
     *
     * @param authentication 인증 객체
     * @return GoogleRes 최종 응답 객체
     */
    private GoogleRes createGoogleResponse(Authentication authentication) {
        // JWT를 생성합니다.
        AuthResDto authResponse = tokenProvider.createAuthResponse(authentication);

        // 사용자 정보를 CustomUserDetails로 변환합니다.
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // JWT와 사용자 설정 완료 여부를 포함한 GoogleRes 응답 객체를 반환합니다.
        return new GoogleRes(authResponse, userDetails.getIsCompleted());
    }
}
