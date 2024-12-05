package com.trekker.domain.project.dto;

import java.time.LocalDate;

public record ProjectSkillDto(
        Long projectId,
        String projectName,
        String projectDescription,
        LocalDate startDate, // 프로젝트 시작 날짜
        LocalDate endDate, // 프로젝트 종료 날짜
        String skillType,
        String skillName,
        Long skillCount
){

}
