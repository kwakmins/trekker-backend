package com.trekker.domain.member.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.trekker.domain.member.dto.req.MemberUpdateReqDto;
import com.trekker.domain.member.dto.req.OnboardingReqDto;
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

    @Column(name = "profile_image", length = 512)
    private String profileImage;

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
    public Member(Long id, String email, Role role, String name,String profileImage, SocialProvider socialProvider,
            Job job, Onboarding onboarding, List<Project> projectList) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
        this.profileImage = profileImage;
        this.job = job;
        this.onboarding = onboarding;
        this.socialProvider = socialProvider;
        this.projectList = projectList;
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

        if (onboardingReqDto.projectReqDto() != null) {
            Project project = onboardingReqDto.projectReqDto().toEntity(this);
            this.projectList.add(project);
        }
        onboarding.updateCompleted();
    }

    public void updateMember(MemberUpdateReqDto reqDto) {
        this.name = reqDto.name();
        this.job.updateJobName(reqDto.jobName());
    }

    public void updateProfileImage(String profileImagePath) {
        this.profileImage = profileImagePath;
    }
}