package com.trekker.domain.calender.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.trekker.domain.calender.dto.res.MonthlyTaskSummaryDto;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.dto.res.TaskResDto;
import com.trekker.domain.task.entity.Task;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalenderServiceTest {

    @InjectMocks
    CalenderService calenderService;
    @Mock
    TaskRepository taskRepository;

    private Long memberId = 1L;

    @Test
    void getMonthlyCalender() {
        // given
        int year = 2024;
        int month = 12;
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<MonthlyTaskSummaryDto> mockTasks = List.of(
                new MonthlyTaskSummaryDto(LocalDate.of(year, month, 1),
                        LocalDate.of(year, month, 5), "Task 1"),
                new MonthlyTaskSummaryDto(LocalDate.of(year, month, 10),
                        LocalDate.of(year, month, 15), "Task 2")
        );

        when(taskRepository.getMonthlyTaskSummary(memberId, startOfMonth, endOfMonth)).thenReturn(
                mockTasks);

        // when
        List<MonthlyTaskSummaryDto> result = calenderService.getMonthlyCalender(memberId, year,
                month);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).name()).isEqualTo("Task 1");
        assertThat(result.get(1).name()).isEqualTo("Task 2");
    }

    @Test
    void getTodayTasks() {
        // given
        LocalDate today = LocalDate.now();
        Task task1 = Task.builder()
                .id(1L)
                .name("Task 1")
                .start_date(today)
                .end_date(today.plusDays(1))
                .isCompleted(false)
                .build();
        Task task2 = Task.builder()
                .id(2L)
                .name("Task 2")
                .start_date(today.minusDays(1))
                .end_date(today.plusDays(1))
                .isCompleted(false)
                .build();
        List<Task> taskList = List.of(task1, task2);

        when(taskRepository.findTasksForToday(memberId, today)).thenReturn(taskList);

        // when
        List<TaskResDto> result = calenderService.getTodayTasks(memberId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).name()).isEqualTo(taskList.get(0).getName());
        assertThat(result.get(1).name()).isEqualTo(taskList.get(1).getName());
    }
}