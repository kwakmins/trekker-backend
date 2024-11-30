package com.trekker.domain.task.util;

import com.trekker.domain.task.entity.Task;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * 태스크(Task)와 관련된 유틸리티 메서드를 제공하는 클래스입니다.
 * 날짜 범위 확인 및 특정 날짜에 완료된 태스크 여부를 판단하는 기능을 포함합니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskFilter {

    /**
     * 특정 날짜(reqDate)가 주어진 날짜 범위(startDate ~ endDate)에 포함되는지 확인합니다.
     *
     * @param reqDate   기준 날짜
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜 (nullable)
     * @return 범위 내에 포함되면 true, 그렇지 않으면 false
     */
    public static boolean isDateWithinRange(LocalDate reqDate, LocalDate startDate,
            LocalDate endDate) {
        if (endDate == null) {
            return startDate.isEqual(reqDate);
        }
        return !startDate.isAfter(reqDate) && !endDate.isBefore(reqDate);
    }

    /**
     * 특정 날짜(date)에 완료된 태스크가 존재하는지 확인합니다.
     *
     * @param tasks 태스크 리스트
     * @param date  기준 날짜
     * @return 해당 날짜에 완료된 태스크가 존재하면 true, 아니면 false
     */
    public static boolean isTaskCompletedOnDate(List<Task> tasks, LocalDate date) {
        return tasks.stream()
                .anyMatch(task ->
                        !date.isBefore(task.getStartDate()) && // date >= startDate
                                (task.getEndDate() == null || !date.isAfter(task.getEndDate())) && // endDate가 null이거나 date <= endDate
                                task.getIsCompleted() // 완료된 태스크
                );
    }
}
