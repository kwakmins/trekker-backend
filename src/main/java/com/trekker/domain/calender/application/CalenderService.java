package com.trekker.domain.calender.application;

import com.trekker.domain.calender.dto.res.MonthlyTaskSummaryDto;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.dto.res.TaskResDto;
import com.trekker.domain.task.entity.Task;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalenderService {

    private final TaskRepository taskRepository;


    /**
     * 년과 월을 받아 월간 데이터를 생성합니다.
     *
     * @param year    조회할 연도
     * @param month   조회할 월
     * @param memberId 사용자의 Id
     * @return 월간 데이터와 날짜별 할 일 리스트
     */
    public List<MonthlyTaskSummaryDto> getMonthlyCalender(Long memberId, int year, int month) {
        // 월의 시작일과 종료일 계산
        LocalDate startOfMonth = LocalDate.of(year,month,1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        // 월간 데이터 조회
        return taskRepository.getMonthlyTaskSummary(memberId, startOfMonth, endOfMonth);
    }

    /**
     * 오늘의 할 일을 조회합니다.
     * @param memberId 조회할 회원의 ID
     * @return 오늘 날짜 기준 사용자의 할 일
     */
    public List<TaskResDto> getTodayTasks(Long memberId) {
        // 오늘 날짜 계산
        LocalDate today = LocalDate.now();

        // 오늘의 할 일 목록 조회
        List<Task> tasksForToday = taskRepository.findTasksForToday(memberId, today);

        // DTO로 변환 및 반환
        return tasksForToday.stream()
                .map(TaskResDto::toDto)
                .toList();
    }
}
