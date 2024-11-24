package com.trekker.global.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //GLOBAL
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류"),

    //Security
    ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, "필요한 접근 권한이 없습니다."),
    ACCESS_AUTH_ENTRY_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효한 자격이 없습니다."),

    //JWT
    INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다.");

    //오류 상태코드
    private final HttpStatus httpStatus;
    //오류 메시지
    private final String message;
}
