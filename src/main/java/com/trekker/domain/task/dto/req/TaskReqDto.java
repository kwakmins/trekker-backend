package com.trekker.domain.task.dto.req;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TaskReqDto(

        @Schema(description = "작업 이름", example = "API 개발")
        @NotNull
        String name,

        @Schema(description = "작업 시작 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        @NotNull
        LocalDate startDate,

        @Schema(description = "작업 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-01-10")
        LocalDate endDate

) {

    public Task toEntity(Project project) {
        return Task.builder()
                .name(this.name)
                .start_date(this.startDate)
                .end_date(this.endDate)
                .isCompleted(project.getIsCompleted())
                .project(project)
                .build();
    }
}