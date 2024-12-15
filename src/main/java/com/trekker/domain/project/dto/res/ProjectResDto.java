package com.trekker.domain.project.dto.res;

import com.trekker.domain.project.entity.Project;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ProjectResDto(

        Long id,

        String type,

        String title,

        String description,

        LocalDate startDate,

        LocalDate endDate,
        // 진행률
        int progress
) {

    public static ProjectResDto toDto(Project project, int progress) {
        return ProjectResDto.builder()
                .id(project.getId())
                .type(project.getType())
                .title(project.getTitle())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .description(project.getDescription())
                .progress(progress)
                .build();
    }

}