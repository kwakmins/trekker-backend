package com.trekker.domain.project.dto.req;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.entity.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ProjectReqDto(

        @Schema(description = "프로젝트 제목", example = "개발 프로젝트")
        @NotNull
        @Size(max = 20, message = "제목은 최대 20 자까지 입력 가능합니다.")
        String title,

        @Schema(description = "프로젝트 설명", example = "백엔드 프로젝트 설명")
        @NotNull
        @Size(max = 50, message = "설명은 최대 50 자까지 입력 가능합니다.")
        String description,

        @Schema(description = "프로젝트 시작 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        @NotNull
        LocalDate startDate,

        @Schema(description = "프로젝트 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-06-01")
        LocalDate endDate,

        @Schema(description = "프로젝트 유형 (개인, 팀)", example = "팀")
        @NotNull
        String type // 프로젝트 유형 (개인 , 팀)
) {

    public Project toEntity(Member member) {
        return Project.builder()
                .title(this.title)
                .description(this.description)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .isCompleted(false)
                .type(this.type)
                .member(member)
                .build();
    }
}