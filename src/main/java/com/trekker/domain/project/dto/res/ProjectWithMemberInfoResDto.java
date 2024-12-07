package com.trekker.domain.project.dto.res;

import com.trekker.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record ProjectWithMemberInfoResDto(

        @Schema(description = "회원 이름", example = "홍길동")
        String name,

        @Schema(description = "회원 직업 이름", example = "백엔드 개발자")
        String jobName,

        @Schema(description = "프로젝트 목록")
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