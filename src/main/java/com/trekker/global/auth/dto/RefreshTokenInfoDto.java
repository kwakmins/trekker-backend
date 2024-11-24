package com.trekker.global.auth.dto;

import lombok.Getter;

@Getter
public record RefreshTokenInfoDto(String userAccount, String refreshToken, String authorities) {

}
