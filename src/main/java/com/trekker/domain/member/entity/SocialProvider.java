package com.trekker.domain.member.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "social_providers")
public class SocialProvider {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "social_provider_id", nullable = false)
    private Long id;

    // 소셜 로그인 제공자 정보 (예: kakao, naver 등)
    @Column(name = "provider")
    private String provider;

    // 소셜 로그인 제공자의 고유 Id
    @Column(name = "provider_id", unique = true)
    private String providerId;

    @Builder
    public SocialProvider(Long id, String provider, String providerId) {
        this.id = id;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static SocialProvider toSocialProvider(String provider, String providerId) {
        return SocialProvider.builder()
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
