package com.trekker.domain.project.dto.res;

import com.trekker.domain.project.entity.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ProjectResDto(

        @Schema(description = "프로젝트 ID", example = "1")
        Long id,

        @Schema(description = "프로젝트 유형 (개인, 팀)", example = "팀")
        String type,

        @Schema(description = "프로젝트 제목", example = "백엔드 개발 프로젝트")
        String title,

        @Schema(description = "프로젝트 설명", example = "Spring Boot와 MySQL을 사용한 RESTful API 개발")
        String description,

        @Schema(description = "프로젝트 시작 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        LocalDate startDate,

        @Schema(description = "프로젝트 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-06-01")
        LocalDate endDate,

        @Schema(description = "프로젝트 진행률 (%)", example = "100")
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