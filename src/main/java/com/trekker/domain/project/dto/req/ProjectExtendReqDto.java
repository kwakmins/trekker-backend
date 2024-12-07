package com.trekker.domain.project.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProjectExtendReqDto(

        @Schema(description = "프로젝트 연장 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-12-31")
        @NotNull
        LocalDate endDate
) {}