package com.trekker.domain.member.dto;

import com.trekker.domain.project.dto.ProjectAddReqDto;

public record OnboardingReqDto(String name, String jobName, ProjectAddReqDto projectAddDto) {

}
