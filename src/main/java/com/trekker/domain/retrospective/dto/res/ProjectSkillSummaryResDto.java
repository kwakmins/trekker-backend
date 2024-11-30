package com.trekker.domain.retrospective.dto.res;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.dto.SkillCountDto;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ProjectSkillSummaryResDto(
        Long projectId,
        String title,
        LocalDate startDate,
        LocalDate endDate,

        //상위 3개 반환
        List<SkillCountDto> topSoftSkillList,
        List<SkillCountDto> topHardSkillList

) {

    public static ProjectSkillSummaryResDto toDto(Project project,
            List<SkillCountDto> topSoftSkillList, List<SkillCountDto> topHardSkillList) {
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
