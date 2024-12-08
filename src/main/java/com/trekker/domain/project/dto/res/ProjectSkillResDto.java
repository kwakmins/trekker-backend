package com.trekker.domain.project.dto.res;

import com.trekker.domain.project.dto.ProjectSkillDto;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ProjectSkillResDto(
        String projectName,
        String projectDescription,
        LocalDate startDate,   // 프로젝트 시작 날짜
        LocalDate endDate,     // 프로젝트 종료 날짜
        List<String> softSkillList,
        List<String> hardSkillList

) {

    public static ProjectSkillResDto toDto(ProjectSkillDto project,List<String> softSkillList,
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