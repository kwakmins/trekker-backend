package com.trekker.domain.project.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProjectWithTaskCompletedList(

        @Schema(description = "프로젝트 ID", example = "1")
        Long projectId,

        @Schema(description = "프로젝트 제목", example = "백엔드 개발 프로젝트")
        String title,

        @Schema(description = "완료된 작업 개수(회고 작성 개수)", example = "10")
        Long completedCount
) {

}