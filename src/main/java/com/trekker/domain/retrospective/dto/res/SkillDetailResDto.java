package com.trekker.domain.retrospective.dto.res;

import java.time.LocalDate;

public record SkillDetailResDto(
        LocalDate startDate,
        LocalDate endDate,
        String taskName,
        String retrospectiveContent
) {

}
