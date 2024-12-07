package com.trekker.domain.calender.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MonthlyTaskSummaryDto(

        @Schema(description = "시작일 (YYYY-MM-DD 형식)", example = "2024-12-01")
        LocalDate startDate,

        @Schema(description = "종료일 (YYYY-MM-DD 형식)", example = "2024-12-31")
        LocalDate endDate,

        @Schema(description = "할 일 이름", example = "프로젝트 발표 준비")
        String name
) {

}