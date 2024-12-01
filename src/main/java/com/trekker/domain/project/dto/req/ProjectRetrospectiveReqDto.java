package com.trekker.domain.project.dto.req;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.project.entity.ProjectRetrospective;
import jakarta.validation.constraints.Size;

public record ProjectRetrospectiveReqDto(
        @Size(max = 300, message = "제목은 최대 20 자까지 입력 가능합니다.")
        String content
) {
    public ProjectRetrospective toEntity(Project project) {
        return ProjectRetrospective.builder()
                .project(project)
                .content(this.content)
                .build();
    }

}
