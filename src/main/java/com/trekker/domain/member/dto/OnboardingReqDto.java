package com.trekker.domain.member.dto;

import com.trekker.domain.project.dto.ProjectAddReqDto;
import jakarta.validation.constraints.NotNull;

public record OnboardingReqDto(
        @NotNull String name,
        @NotNull String jobName,
        ProjectAddReqDto projectAddDto) {}
