package com.trekker.domain.member.dto.req;

import com.trekker.domain.project.dto.req.ProjectReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OnboardingReqDto(

        @Schema(description = "회원 이름", example = "홍길동")
        @NotNull
        String name,

        @Schema(description = "회원 직업 이름", example = "백엔드 개발자")
        @NotNull
        String jobName,

        @Schema(description = "회원의 주요 프로젝트 정보")
        ProjectReqDto projectReqDto
) {}