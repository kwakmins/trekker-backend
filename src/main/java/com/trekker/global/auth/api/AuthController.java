package com.trekker.global.auth.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.global.auth.application.AuthService;
import com.trekker.global.auth.dto.req.RefreshTokenReqDto;
import com.trekker.global.auth.dto.res.AuthResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken
    ) {
        String token = resolveToken(accessToken);
        authService.logout(token);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(
            @LoginMember String email
    ) {
        authService.deleteAccount(email);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResDto> refreshAccessToken(
            @RequestBody RefreshTokenReqDto refreshTokenRequestDto
    ) {
        AuthResDto authResponseDto = authService.refreshAccessToken(
                refreshTokenRequestDto.refreshToken());
        return ResponseEntity.ok(authResponseDto);
    }

    /**
     * 임시 토큰을 검증하고 최종 액세스 및 리프레시 토큰 발급
     */
    @GetMapping("/issue-final-token")
    public ResponseEntity<?> issueFinalTokens(
            @NotNull @RequestParam("tempToken") String tempToken
    ) {
        AuthResDto authResponse = authService.retrieveAuthResponse(tempToken);

        return ResponseEntity.ok(authResponse);
    }

    private String resolveToken(String accessToken) {
        if (accessToken.startsWith(BEARER_PREFIX)) {
            return accessToken.substring(BEARER_PREFIX.length());
        }
        return accessToken;
    }
}