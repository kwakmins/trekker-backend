package com.trekker.domain.calender.application;

import com.trekker.domain.calender.dto.res.MonthlyTaskSummaryDto;
import com.trekker.domain.task.dao.TaskRepository;
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

}
