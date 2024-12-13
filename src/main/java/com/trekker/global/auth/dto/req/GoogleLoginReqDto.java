package com.trekker.global.auth.dto.req;

public record GoogleLoginReqDto(
        String email,
        String googleId,
        String accessToken
) {

}
