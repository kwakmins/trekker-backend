package com.trekker.global.config.security.annotation;

import com.trekker.global.auth.custom.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

/**
 * @LoginMember에서 사용되는 유틸리티 클래스
 */
public class LoginMemberUtil {

    /**
     * 인증된 Principal 객체를 반환하거나 예외를 발생
     *
     * @param principal 인증된 사용자 정보
     * @return userId 사용자 ID (Long)
     * @throws AuthenticationCredentialsNotFoundException 인증되지 않은 경우
     */
    public static Long getPrincipalOrThrow(Object principal) {
        if (principal instanceof CustomUserDetails) {
            try {
                // String 타입의 ID를 Long으로 변환
                return Long.valueOf(((CustomUserDetails) principal).getId());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID 값이 Long으로 변환할 수 없습니다: " + ((CustomUserDetails) principal).getId());
            }
        }

        // 인증되지 않았거나 예상치 못한 객체 타입일 경우 예외 발생
        throw new AuthenticationCredentialsNotFoundException("사용자 인증 정보가 필요합니다.");
    }
}