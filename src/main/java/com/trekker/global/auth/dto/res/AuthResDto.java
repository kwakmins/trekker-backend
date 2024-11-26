package com.trekker.global.auth.dto.res;

public record AuthResDto(String accessToken, String refreshToken, boolean refreshTokenWillExpire) {

}
