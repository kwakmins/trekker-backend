package com.trekker.domain.retrospective.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record SkillDetailResDto(

        @Schema(description = "작업 ID", example = "101")
        Long taskId,

        @Schema(description = "작업 시작 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        LocalDate startDate,

        @Schema(description = "작업 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-01-10")
        LocalDate endDate,

        @Schema(description = "작업 이름", example = "백엔드 API 개발")
        String taskName,

        @Schema(description = "회고 내용", example = "Spring Boot와 JPA를 사용하여 RESTful API를 개발했습니다.")
        String retrospectiveContent
) {

}