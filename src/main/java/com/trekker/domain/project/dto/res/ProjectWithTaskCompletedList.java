package com.trekker.domain.project.dto.res;

public record ProjectWithTaskCompletedList(
        Long projectId,
        String title,
        Long completedCount
) {

}
