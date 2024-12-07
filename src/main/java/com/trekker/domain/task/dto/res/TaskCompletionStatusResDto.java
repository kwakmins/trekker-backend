package com.trekker.domain.task.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TaskCompletionStatusResDto(

        @Schema(description = "날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        LocalDate date,

        @Schema(description = "해당 날짜에 작업을 하나라도 수행했는지 여부", example = "true")
        boolean isCompleted
) { }