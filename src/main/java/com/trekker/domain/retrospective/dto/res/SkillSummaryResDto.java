package com.trekker.domain.retrospective.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record SkillSummaryResDto(

        @Schema(description = "스킬 ID", example = "1")
        Long skillId,

        @Schema(description = "스킬 이름", example = "Spring Boot")
        String skillName,

        @Schema(description = "스킬 사용 횟수", example = "15")
        Long count
) {

}