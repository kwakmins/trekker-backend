package com.trekker.domain.project.dto.res;

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

    public static ProjectResDto toDto(Project project,int progress) {
        return ProjectResDto.builder()
                .id(project.getId())
                .type(project.getType())
                .title(project.getTitle())
                .description(project.getDescription())
                .progress(progress)
                .build();
    }

}
