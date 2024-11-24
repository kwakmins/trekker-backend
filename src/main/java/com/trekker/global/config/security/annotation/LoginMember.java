package com.trekker.global.config.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 인증된 사용자 정보를 가져오는 어노테이션
 * 인증되지 않은 경우 예외를 발생시킴
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "T(com.trekker.global.config.security.annotation.LoginMemberUtil).getPrincipalOrThrow(#this)")
public @interface LoginMember {

    boolean required() default true;
}