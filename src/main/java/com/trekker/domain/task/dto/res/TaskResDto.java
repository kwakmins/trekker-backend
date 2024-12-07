package com.trekker.domain.task.dto.res;

import com.trekker.domain.task.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TaskResDto(

        @Schema(description = "할 일 ID", example = "101")
        Long taskId,

        @Schema(description = "할 일 이름", example = "프로젝트 발표 준비")
        String name,

        @Schema(description = "시작일 (YYYY-MM-DD 형식)", example = "2024-12-01")
        LocalDate start_date,

        @Schema(description = "종료일 (YYYY-MM-DD 형식)", example = "2024-12-02")
        LocalDate end_date,

        @Schema(description = "완료 여부", example = "true")
        Boolean isCompleted
) {

    public static TaskResDto toDto(Task task) {
        return TaskResDto.builder()
                .taskId(task.getId())
                .name(task.getName())
                .start_date(task.getStartDate())
                .end_date(task.getEndDate())
                .isCompleted(task.getIsCompleted())
                .build();
    }
}