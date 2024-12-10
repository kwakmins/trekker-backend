package com.trekker.domain.project.dto.req;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.entity.Project;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ProjectReqDto(

        @NotNull
        @Size(max = 20, message = "제목은 최대 20 자까지 입력 가능합니다.")
        String title,

        @NotNull
        @Size(max = 50, message = "설명은 최대 50 자까지 입력 가능합니다.")
        String description,

        @NotNull
        LocalDate startDate,

        LocalDate endDate,

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