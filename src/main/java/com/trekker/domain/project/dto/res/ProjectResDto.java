package com.trekker.domain.project.dto.res;

import com.trekker.domain.project.application.ProjectProgressCalculator;
import com.trekker.domain.project.entity.Project;
import lombok.Builder;

@Builder
public record ProjectResDto (
       Long id,
       String type,
       String title,
       String description,
       //진행률
       int progress
){

    public static ProjectResDto toDto(Project project) {
        // 진행률 계산
        int calculateProgress = ProjectProgressCalculator.calculateProgress(project.getStartDate(),
                project.getEndDate());

        return ProjectResDto.builder()
                .id(project.getId())
                .type(project.getType())
                .title(project.getTitle())
                .description(project.getDescription())
                .progress(calculateProgress)
                .build();
    }

}
