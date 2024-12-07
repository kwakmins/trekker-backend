package com.trekker.domain.member.dto.res;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dto.res.ProjectSkillResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberPortfolioResDto(

        @Schema(description = "회원 이름", example = "홍길동")
        String name,

        @Schema(description = "회원 직업 이름", example = "백엔드 개발자")
        String jobName,

        @Schema(description = "프로필 이미지 경로", example = "/uploads/profile-images/profile123.jpg")
        String profileImage,

        @Schema(description = "프로젝트 및 기술 목록")
        List<ProjectSkillResDto> projectSkillResDto
) {

    public static MemberPortfolioResDto toDto(Member member,
            List<ProjectSkillResDto> projectSkillResDto) {
        return MemberPortfolioResDto.builder()
                .name(member.getName())
                .jobName(member.getJob().getJobName())
                .profileImage(member.getProfileImage())
                .projectSkillResDto(projectSkillResDto)
                .build();
    }
}