package com.trekker.domain.project.dto.res;

import com.trekker.domain.project.dto.ProjectSkillDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ProjectSkillResDto(

        @Schema(description = "프로젝트 이름", example = "백엔드 개발 프로젝트")
        String projectName,

        @Schema(description = "프로젝트 설명", example = "Spring Boot와 MySQL을 사용한 RESTful API 개발")
        String projectDescription,

        @Schema(description = "프로젝트 시작 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        LocalDate startDate,

        @Schema(description = "프로젝트 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-06-01")
        LocalDate endDate,

        @Schema(description = "소프트 스킬 목록", example = "[\"커뮤니케이션\", \"팀워크\"]")
        List<String> softSkillList,

        @Schema(description = "하드 스킬 목록", example = "[\"Spring Boot\", \"MySQL\"]")
        List<String> hardSkillList

) {

    public static ProjectSkillResDto toDto(ProjectSkillDto project, List<String> softSkillList,
            List<String> hardSkillList) {

        return ProjectSkillResDto.builder()
                .projectName(project.projectName())
                .projectDescription(project.projectDescription())
                .startDate(project.startDate())
                .endDate(project.endDate())
                .softSkillList(softSkillList)
                .hardSkillList(hardSkillList)
                .build();
    }
}