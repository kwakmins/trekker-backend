package com.trekker.global.auth.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshTokenResDto(

        @Schema(description = "새로운 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken
) { }