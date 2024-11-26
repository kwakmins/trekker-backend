package com.trekker.domain.member.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.trekker.domain.member.dto.OnboardingReqDto;
import com.trekker.domain.project.entity.Project;
import com.trekker.global.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
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

    @OneToOne(fetch = LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "social_provider_id")
    private SocialProvider socialProvider;

    @OneToOne(fetch = LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "job_id")
    private Job job;

    @OneToOne(fetch = LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "onboarding_id")
    private Onboarding onboarding;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Project> projectList = new ArrayList<>();


    @Builder
    public Member(Long id, String email, Role role, String name, SocialProvider socialProvider,
            Job job, Onboarding onboarding) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
        this.job = job;
        this.onboarding = onboarding;
        this.socialProvider = socialProvider;
    }

    public static Member toMember(String email, Role role, String provider, String providerId) {
        SocialProvider socialProvider = SocialProvider.toSocialProvider(provider, providerId);
        Onboarding onboarding = Onboarding.toOnboarding();
        return Member.builder()
                .email(email)
                .role(role)
                .socialProvider(socialProvider)
                .onboarding(onboarding)
                .build();
    }

    public void updateOnboarding(OnboardingReqDto onboardingReqDto) {
        Job job = Job.toJob(onboardingReqDto.jobName());
        this.name = onboardingReqDto.name();
        this.job = job;

        if (onboardingReqDto.projectAddDto() != null) {
            Project project = onboardingReqDto.projectAddDto().toEntity();
            project.updateMember(this);
            this.projectList.add(project);
        }
        onboarding.updateCompleted();
    }
}