package com.trekker.domain.task.dto;

import java.time.LocalDate;

public record TaskRetrospectiveSkillDto(
        Long taskId,
        LocalDate startDate,
        LocalDate endDate,
        String retrospectiveContent,
        String skillType,
        String skillName
) {

}
