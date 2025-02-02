package com.trekker.global.auth.custom;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Builder
public class CustomUserDetails implements OAuth2User {

    private String id;
    // 온보딩 완료 여부
    private boolean isCompleted;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    @Override
    public String getName() {
        return id;
    }

    /**
     * 사용자의 권한 정보 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public boolean getIsCompleted() {
        return this.isCompleted;
    }

}
