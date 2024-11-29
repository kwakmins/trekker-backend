package com.trekker.domain.task.dto.req;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.entity.Task;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TaskReqDto(
        @NotNull
        String name,
        @NotNull
        LocalDate startDate,

        LocalDate endDate

) {

    public Task toEntity(Project project, String status) {
        return Task.builder()
                .name(this.name)
                .start_date(this.startDate)
                .end_date(this.endDate)
                .status(status)
                .project(project)
                .build();
    }
}