package com.trekker.domain.retrospective.dto.res;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.dto.SkillCountDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ProjectSkillSummaryResDto(

        @Schema(description = "프로젝트 ID", example = "1")
        Long projectId,

        @Schema(description = "프로젝트 제목", example = "백엔드 개발 프로젝트")
        String title,

        @Schema(description = "프로젝트 시작 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        LocalDate startDate,

        @Schema(description = "프로젝트 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-06-01")
        LocalDate endDate,

        @Schema(description = "상위 3개 소프트 스킬 목록")
        List<SkillCountDto> topSoftSkillList,

        @Schema(description = "상위 3개 하드 스킬 목록")
        List<SkillCountDto> topHardSkillList
) {

    public static ProjectSkillSummaryResDto toDto(Project project,
            List<SkillCountDto> topSoftSkillList,
            List<SkillCountDto> topHardSkillList) {
        return ProjectSkillSummaryResDto.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .topSoftSkillList(topSoftSkillList)
                .topHardSkillList(topHardSkillList)
                .build();
    }
}