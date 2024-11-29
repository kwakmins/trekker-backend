package com.trekker.domain.project.dto.res;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.dto.res.TaskCompletionStatusResDto;
import com.trekker.domain.task.dto.res.TaskResDto;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
@Builder
public record ProjectWithTaskInfoResDto(
        String title,
        String type,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        // 주간 성취 달력
        List<TaskCompletionStatusResDto> weeklyAchievement,
        // 선택한 날짜의 할 일 목록
        List<TaskResDto> taskList
) {

    public static ProjectWithTaskInfoResDto toDto(Project project,
            List<TaskCompletionStatusResDto> weeklyAchievement, List<TaskResDto> taskList) {
        return ProjectWithTaskInfoResDto.builder()
                .title(project.getTitle())
                .type(project.getType())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .weeklyAchievement(weeklyAchievement)
                .taskList(taskList)
                .build();
    }
}