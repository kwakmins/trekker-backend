package com.trekker.domain.task.dto.res;

import com.trekker.domain.task.dto.TaskRetrospectiveSkillDto;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record TaskRetrospectiveResDto(
        LocalDate startDate,
        LocalDate endDate,
        String retrospectiveContent,
        List<String> softSkillList,
        List<String> hardSkillList
) {

    public static TaskRetrospectiveResDto toDto(TaskRetrospectiveSkillDto taskDto, List<String> softSkillList, List<String> hardSkillList) {
        return TaskRetrospectiveResDto.builder()
                .startDate(taskDto.startDate())
                .endDate(taskDto.endDate())
                .retrospectiveContent(taskDto.retrospectiveContent())
                .softSkillList(softSkillList)
                .hardSkillList(hardSkillList)
                .build();
    }
}