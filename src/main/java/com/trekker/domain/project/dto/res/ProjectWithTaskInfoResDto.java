package com.trekker.domain.project.dto.res;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.dto.res.TaskCompletionStatusResDto;
import com.trekker.domain.task.dto.res.TaskResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ProjectWithTaskInfoResDto(

        @Schema(description = "프로젝트 제목", example = "백엔드 개발 프로젝트")
        String title,

        @Schema(description = "프로젝트 유형 (예: 개인, 팀)", example = "팀")
        String type,

        @Schema(description = "프로젝트 설명", example = "Spring Boot와 MySQL을 사용한 RESTful API 개발")
        String description,

        @Schema(description = "프로젝트 시작 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        LocalDate startDate,

        @Schema(description = "프로젝트 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-06-30")
        LocalDate endDate,

        @Schema(description = "주간 성취 달력")
        List<TaskCompletionStatusResDto> weeklyAchievement,

        @Schema(description = "선택한 날짜의 작업(Task) 목록")
        List<TaskResDto> taskList
) {

    public static ProjectWithTaskInfoResDto toDto(Project project,
            List<TaskCompletionStatusResDto> weeklyAchievement,
            List<TaskResDto> taskList) {
        return ProjectWithTaskInfoResDto.builder()
                .title(project.getTitle())
                .type(project.getType())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .weeklyAchievement(weeklyAchievement)
                .taskList(taskList)
                .build();
    }
}