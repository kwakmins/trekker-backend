package com.trekker.domain.task.dto.res;

import com.trekker.domain.task.entity.Task;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TaskResDto(

        Long taskId,

        String name,

        LocalDate start_date,

        LocalDate end_date,

        Boolean isCompleted
) {

    public static TaskResDto toDto(Task task) {
        return TaskResDto.builder()
                .taskId(task.getId())
                .name(task.getName())
                .start_date(task.getStartDate())
                .end_date(task.getEndDate())
                .isCompleted(task.getIsCompleted())
                .build();
    }
}