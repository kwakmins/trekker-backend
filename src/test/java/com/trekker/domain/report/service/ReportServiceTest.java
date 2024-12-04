package com.trekker.domain.report.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.trekker.domain.report.dto.ReportResDto;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.dto.SkillCountDto;
import com.trekker.domain.task.entity.Task;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    ReportService reportService;
    @Mock
    private RetrospectiveSkillRepository retrospectiveSkillRepository;
    @Mock
    private TaskRepository taskRepository;

    private Long memberId;
    private List<SkillCountDto> mockSoftSkillList;
    private List<SkillCountDto> mockHardSkillList;
    private LocalDate today;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        today = LocalDate.now();

        mockSoftSkillList = Arrays.asList(
                new SkillCountDto("Communication", 5L),
                new SkillCountDto("Teamwork", 4L),
                new SkillCountDto("Adaptability", 3L)
        );

        mockHardSkillList = Arrays.asList(
                new SkillCountDto("Java", 6L),
                new SkillCountDto("Spring Boot", 4L),
                new SkillCountDto("SQL", 3L),
                new SkillCountDto("Spring", 2L)
        );

        task1 = Task.builder()
                .id(1L)
                .name("미완료 할 일")
                .isCompleted(false)
                .start_date(today)
                .build();

        task2 = Task.builder()
                .id(2L)
                .name("완료 할 일")
                .isCompleted(true)
                .start_date(today)
                .end_date(today.plusDays(3))
                .build();
    }


    @DisplayName("회원의 리포트를 조회합니다.")
    @Test
    void getMemberReport() {
        // given
        LocalDate startOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        when(retrospectiveSkillRepository.findTopSkillsByMemberIdAndType(memberId, "소프트",
                PageRequest.of(0, 3))).thenReturn(mockSoftSkillList);
        when(retrospectiveSkillRepository.findTopSkillsByMemberIdAndType(memberId, "하드",
                PageRequest.of(0, 3))).thenReturn(mockHardSkillList);
        when(taskRepository.findTasksInMonth(memberId, startOfMonth, endOfMonth)).thenReturn(
                List.of(task1, task2));

        // when
        ReportResDto memberReport = reportService.getMemberReport(memberId);

        // then
        assertThat(memberReport.softSkillList()).isEqualTo(mockSoftSkillList);
        assertThat(memberReport.hardSkillList()).isEqualTo(mockHardSkillList);


        // 오늘의 경우 할 일 2개중 1개만 완료 50% -> 40
        assertThat(memberReport.dailyProgressRatesInMonth().get(today)).isEqualTo(40);
        assertThat(memberReport.dailyProgressRatesInMonth().get(today.plusDays(1))).isEqualTo(100);
        // 오늘 완료한 할일은 1개(task2)
        assertThat(memberReport.weeklyCompletedTasks().get(today)).isEqualTo(1);

    }

    @DisplayName("회원의 전체 스킬 리스트를 조회합니다.")
    @Test
    void getMemberSkillList() {
        // given
        String type = "하드";
        when(retrospectiveSkillRepository.findSkillsByMemberIdAndType(memberId, type)).thenReturn(
                mockHardSkillList);

        // when
        List<SkillCountDto> memberSkillList = reportService.getMemberSkillList(memberId, type);

        // then
        assertThat(memberSkillList).isEqualTo(mockHardSkillList);
        assertThat(memberSkillList.size()).isEqualTo(4);
    }
}