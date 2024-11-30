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
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),

    //Social
    SOCIAL_UNLINK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "소셜 연결 해제에 실패했습니다."),
    UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 제공자입니다."),

    //Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    MEMBER_ONBOARDING_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "온보딩을 이미 완료했습니다."),

    //Project
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "프로젝트 정보를 찾을 수 없습니다."),
    PROJECT_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    //Task
    TASK_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "할 일 정보를 찾을 수 없습니다."),

    //Retrospective
    RETROSPECTIVE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    RETROSPECTIVE_NOT_FOUND(HttpStatus.NOT_FOUND, "회고를 찾을 수 없습니다.");

    //오류 상태코드
    private final HttpStatus httpStatus;
    //오류 메시지
    private final String message;
}
