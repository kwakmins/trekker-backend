package com.trekker.domain.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SkillCountDto(

        @Schema(description = "스킬 이름", example = "Spring Boot")
        String skillName,

        @Schema(description = "스킬 사용 횟수", example = "15")
        Long count
) {

}