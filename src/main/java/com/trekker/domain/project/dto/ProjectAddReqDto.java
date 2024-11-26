package com.trekker.domain.project.dto;

import com.trekker.domain.project.entity.Project;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProjectAddReqDto(
        @NotNull String title,
        @NotNull String description,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        @NotNull String type // 프로젝트 유형 (개인 , 팀)
) {

    public Project toEntity() {
        return Project.builder()
                .title(this.title)
                .description(this.description)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .isCompleted(false)
                .type(this.type)
                .build();
    }
}
