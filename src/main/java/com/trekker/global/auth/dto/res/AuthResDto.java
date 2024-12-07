package com.trekker.global.auth.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResDto(

        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken,

        @Schema(description = "리프레시 토큰 만료 예정 여부", example = "true")
        boolean refreshTokenWillExpire
) { }