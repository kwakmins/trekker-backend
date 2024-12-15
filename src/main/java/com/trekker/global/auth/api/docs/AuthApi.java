package com.trekker.global.auth.api.docs;

import com.trekker.domain.member.dto.req.MemberWithdrawalReqDto;
import com.trekker.global.auth.dto.req.RefreshTokenReqDto;
import com.trekker.global.auth.dto.res.AuthResDto;
import com.trekker.global.auth.dto.res.RefreshTokenResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import jakarta.validation.constraints.NotNull;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthApi {

    @Operation(
            summary = "로그아웃",
            description = "사용자가 로그아웃합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "로그아웃 성공" )
    ResponseEntity<Void> logout(
            @Parameter(description = "엑세스 토큰", example = "Bearer eyJhbGci..." ) String accessToken
    );

    @Operation(
            summary = "회원 탈퇴",
            description = "사용자가 계정을 삭제합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공" )
    ResponseEntity<Void> deleteAccount(
            @Parameter(hidden = true) Long id,
            MemberWithdrawalReqDto reqDto
    );

    @Operation(
            summary = "엑세스 토큰 재발급",
            description = "리프레시 토큰을 통해 새로운 엑세스 토큰을 재발급합니다."
    )
    @ApiResponse(responseCode = "200", description = "엑세스 토큰 재발급 성공" )
    ResponseEntity<AuthResDto> refreshAccessToken(RefreshTokenReqDto refreshTokenRequestDto);

    @Operation(
            summary = "리프레시 토큰 재발급",
            description = "만료된 리프레시 토큰을 재발급합니다."
    )
    @ApiResponse(responseCode = "200", description = "리프레시 토큰 재발급 성공" )
    ResponseEntity<RefreshTokenResDto> reissueRefreshToken(
            RefreshTokenReqDto refreshTokenRequestDto);

    @Operation(
            summary = "최종 토큰 발급",
            description = "임시 토큰을 검증하고 최종 엑세스 및 리프레시 토큰을 발급합니다."
    )
    @ApiResponse(responseCode = "200", description = "최종 토큰 발급 성공" )
    ResponseEntity<AuthResDto> issueFinalTokens(
            @Parameter(description = "임시 토큰", example = "tempToken12345" ) @NotNull String tempToken
    );
}