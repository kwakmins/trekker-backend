package com.trekker.domain.project.dto.res;

import com.trekker.domain.member.entity.Member;
import java.util.List;
import lombok.Builder;

@Builder
public record ProjectWithMemberInfoResDto(
        String name,
        String jobName,
        List<ProjectResDto> projectList
) {


    public static ProjectWithMemberInfoResDto toDto(Member member,
            List<ProjectResDto> projectList) {
        return ProjectWithMemberInfoResDto.builder()
                .name(member.getName())
                .jobName(member.getJob().getJobName())
                .projectList(projectList)
                .build();
    }
}
