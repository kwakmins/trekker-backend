package com.trekker.global.auth.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.member.dto.req.MemberWithdrawalReqDto;
import com.trekker.global.auth.application.AuthService;
import com.trekker.global.auth.dto.req.RefreshTokenReqDto;
import com.trekker.global.auth.dto.res.AuthResDto;
import com.trekker.global.auth.dto.res.RefreshTokenResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "사용자가 로그아웃합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "로그아웃 성공")
    public ResponseEntity<Void> logout(
            @Parameter(description = "엑세스 토큰", example = "Bearer eyJhbGci...")
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken
    ) {
        String token = resolveToken(accessToken);
        authService.logout(token);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/delete")
    @Operation(
            summary = "회원 탈퇴",
            description = "사용자가 계정을 삭제합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(hidden = true) @LoginMember Long id,
            @RequestBody MemberWithdrawalReqDto reqDto
    ) {
        authService.deleteAccount(id, reqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/access/refresh")
    @Operation(
            summary = "엑세스 토큰 재발급",
            description = "리프레시 토큰을 통해 새로운 엑세스 토큰을 재발급합니다.",
            security = {} // 인증 필요 없음
    )
    @ApiResponse(responseCode = "200", description = "엑세스 토큰 재발급 성공")
    public ResponseEntity<AuthResDto> refreshAccessToken(
            @RequestBody RefreshTokenReqDto refreshTokenRequestDto
    ) {
        AuthResDto authResponseDto = authService.refreshAccessToken(
                refreshTokenRequestDto.refreshToken());
        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/refresh/reissue")
    @Operation(
            summary = "리프레시 토큰 재발급",
            description = "만료된 리프레시 토큰을 재발급합니다.",
            security = {} // 인증 필요 없음
    )
    @ApiResponse(responseCode = "200", description = "리프레시 토큰 재발급 성공")
    public ResponseEntity<RefreshTokenResDto> reissueRefreshToken(
            @RequestBody RefreshTokenReqDto refreshTokenRequestDto
    ) {
        RefreshTokenResDto refreshTokenResDto = authService.reissueRefreshToken(
                refreshTokenRequestDto.refreshToken());

        return ResponseEntity.ok(refreshTokenResDto);
    }

    @GetMapping("/issue-final-token")
    @Operation(
            summary = "최종 토큰 발급",
            description = "임시 토큰을 검증하고 최종 엑세스 및 리프레시 토큰을 발급합니다.",
            security = {} // 인증 필요 없음
    )
    @ApiResponse(responseCode = "200", description = "최종 토큰 발급 성공")
    public ResponseEntity<AuthResDto> issueFinalTokens(
            @Parameter(description = "임시 토큰", example = "tempToken12345")
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