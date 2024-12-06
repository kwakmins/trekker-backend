package com.trekker.domain.member.dto.req;

import com.trekker.domain.project.dto.req.ProjectReqDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OnboardingReqDto(
        @NotNull String name,
        @NotNull String jobName,
        ProjectReqDto projectReqDto) {}
