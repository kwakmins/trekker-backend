package com.trekker.domain.member.dto;

import com.trekker.domain.project.dto.req.ProjectReqDto;
import jakarta.validation.constraints.NotNull;

public record OnboardingReqDto(
        @NotNull String name,
        @NotNull String jobName,
        ProjectReqDto projectReqDto) {}
