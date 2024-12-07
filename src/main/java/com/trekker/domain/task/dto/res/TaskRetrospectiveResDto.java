package com.trekker.domain.task.dto.res;

import com.trekker.domain.task.dto.TaskRetrospectiveSkillDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record TaskRetrospectiveResDto(

        @Schema(description = "작업 ID", example = "101")
        Long taskId,

        @Schema(description = "작업 시작 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        LocalDate startDate,

        @Schema(description = "작업 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-01-07")
        LocalDate endDate,

        @Schema(description = "회고 내용", example = "이 작업을 통해 Spring Boot와 JPA를 심화 학습했습니다.")
        String retrospectiveContent,

        @Schema(description = "소프트 스킬 목록", example = "[\"커뮤니케이션\", \"문제 해결\"]")
        List<String> softSkillList,

        @Schema(description = "하드 스킬 목록", example = "[\"Spring Boot\", \"JPA\"]")
        List<String> hardSkillList
) {

    public static TaskRetrospectiveResDto toDto(TaskRetrospectiveSkillDto taskDto,
            List<String> softSkillList, List<String> hardSkillList) {
        return TaskRetrospectiveResDto.builder()
                .taskId(taskDto.taskId())
                .startDate(taskDto.startDate())
                .endDate(taskDto.endDate())
                .retrospectiveContent(taskDto.retrospectiveContent())
                .softSkillList(softSkillList)
                .hardSkillList(hardSkillList)
                .build();
    }
}