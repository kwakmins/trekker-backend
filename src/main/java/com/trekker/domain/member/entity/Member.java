package com.trekker.domain.member.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.trekker.global.entity.BaseEntity;
import jakarta.persistence.*;

import javax.lang.model.element.Name;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "members")
public class Member extends BaseEntity {

    public static final int MAX_EMAIL_LENGTH = 256;
    public static final int MAX_NAME_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    // 계정의 아이디
    @Column(name = "email", nullable = false, length = MAX_EMAIL_LENGTH)
    private String email;

    //권한
    @Enumerated(STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // 회원의 이름
    @Column(name = "name", length = MAX_NAME_LENGTH)
    private String name;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "social_provider_id")
    private SocialProvider socialProvider;

    @Builder
    public Member(Long id, String email, Role role, String name, SocialProvider socialProvider) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
        this.socialProvider = socialProvider;
    }

    public static Member toMember(String email, Role role, String provider, String providerId) {
        SocialProvider socialProvider = SocialProvider.toSocialProvider(provider, providerId);

        return Member.builder()
                .email(email)
                .role(role)
                .socialProvider(socialProvider)
                .build();
    }

}