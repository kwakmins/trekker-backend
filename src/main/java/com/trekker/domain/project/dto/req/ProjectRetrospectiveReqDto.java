package com.trekker.domain.project.dto.req;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.project.entity.ProjectRetrospective;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record ProjectRetrospectiveReqDto(

        @Schema(description = "회고 내용 (최대 300자)", example = "이 프로젝트를 통해 백엔드 아키텍처 설계 능력을 향상시켰습니다.")
        @Size(max = 300, message = "회고 내용은 최대 300 자까지 입력 가능합니다.")
        String content
) {
    public ProjectRetrospective toEntity(Project project) {
        return ProjectRetrospective.builder()
                .project(project)
                .content(this.content)
                .build();
    }
}