package com.trekker.global.auth.dto;


public record RefreshTokenInfoDto(String userAccount, String refreshToken, String authorities) {

}
