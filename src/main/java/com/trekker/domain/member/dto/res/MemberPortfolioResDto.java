package com.trekker.domain.member.dto.res;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dto.res.ProjectSkillResDto;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberPortfolioResDto (
        String name,
        String jobName,
        // 프로젝트 목록
        List<ProjectSkillResDto> projectSkillResDto

){

    public static MemberPortfolioResDto toDto(Member member,
            List<ProjectSkillResDto> projectSkillResDto) {
        return MemberPortfolioResDto.builder()
                .name(member.getName())
                .jobName(member.getJob().getJobName())
                .projectSkillResDto(projectSkillResDto)
                .build();
    }
}
