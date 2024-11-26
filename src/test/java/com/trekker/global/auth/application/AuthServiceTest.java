package com.trekker.global.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.entity.Member;
import com.trekker.domain.member.entity.SocialProvider;
import com.trekker.global.auth.dto.res.AuthResDto;
import com.trekker.global.config.redis.dao.RedisRepository;
import com.trekker.global.config.security.TokenProvider;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private UnlinkService unlinkService;

    @Test
    void logout() {
        // given
        String jwtToken = "validJwtToken";
        String userId = "12345";
        long expiration = 3600000L;

        when(tokenProvider.getExpiration(jwtToken)).thenReturn(expiration);
        when(tokenProvider.getUserIdFromToken(jwtToken)).thenReturn(userId);

        // when
        authService.logout(jwtToken);

        // then
        verify(redisRepository, times(1)).logoutTokens(jwtToken, expiration, userId);
    }
    @DisplayName("Refresh 토큰을 이용하여 새로운 Access 토큰을 발급한다.")
    @Test
    void refreshAccessToken() {
        // given
        String refreshToken = "validJwtToken";
        String newToken = "newValidJwtToken";
        AuthResDto authResDto = new AuthResDto(newToken, newToken, false);

        when(tokenProvider.refreshAccessToken(refreshToken)).thenReturn(authResDto);

        // when
        AuthResDto authResdto = authService.refreshAccessToken(refreshToken);

        // then
        assertThat(authResdto.accessToken()).isEqualTo("newValidJwtToken");
        verify(tokenProvider, times(1)).refreshAccessToken(refreshToken);
    }

    @DisplayName("유효하지 않은 Refresh 토큰으로 Access 토큰 발급 요청 시 예외가 발생한다.")
    @Test
    void failToRefreshAccessTokenWhenRefreshTokenIsInvalid() {
        // given
        String invalidRefreshToken = "invalidRefreshToken";

        when(tokenProvider.refreshAccessToken(invalidRefreshToken))
                .thenThrow(new BusinessException(invalidRefreshToken, "refreshToken", ErrorCode.INVALID_REFRESH_TOKEN));

        // when & then
        assertThatThrownBy(() -> authService.refreshAccessToken(invalidRefreshToken))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
        verify(tokenProvider, times(1)).refreshAccessToken(invalidRefreshToken);
    }
    @DisplayName("임시 토큰으로 Redis에 저장되어 있는 토큰을 조회한다.")
    @Test
    void retrieveAuthResponse() {
        // given
        String tempToken = "tempToken";
        String validToken = "validJwtToken";
        AuthResDto authResDto = new AuthResDto(validToken, validToken, false);

        when(redisRepository.retrieveAuthResponse(tempToken)).thenReturn(authResDto);

        //when
        AuthResDto resultDto = authService.retrieveAuthResponse(tempToken);

        //then
        assertThat(authResDto).isEqualTo(resultDto);
    }

    @DisplayName("회원 삭제 시 소셜 계정 연결이 끊기고 회원 상태가 삭제 처리된다.")
    @Test
    void deleteAccount() {
        // given
        String email = "test@example.com";
        Member member = Member.builder()
                .email(email)
                .socialProvider(mock(SocialProvider.class))
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // when
        authService.deleteAccount(email);

        // then
        verify(unlinkService, times(1)).unlink(member);
        verify(memberRepository, times(1)).findByEmail(email);
        assertThat(member.isDelete()).isTrue();
    }

    @DisplayName("존재하지 않는 회원 삭제 시 예외가 발생한다.")
    @Test
    void failToDeleteAccountWhenMemberNotFound() {
        // given
        String nonExistentEmail = "notexist@example.com";

        when(memberRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.deleteAccount(nonExistentEmail))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
        verify(memberRepository, times(1)).findByEmail(nonExistentEmail);
        verify(unlinkService, never()).unlink(any(Member.class));
    }
}