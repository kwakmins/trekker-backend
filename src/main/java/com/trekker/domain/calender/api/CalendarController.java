package com.trekker.domain.calender.api;

import com.trekker.domain.calender.application.CalendarService;
import com.trekker.domain.calender.dto.res.MonthlyTaskSummaryDto;
import com.trekker.domain.task.dto.res.TaskResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/month")
    public ResponseEntity<List<MonthlyTaskSummaryDto>> getMonthlyCalendar(
            @LoginMember Long memberId,
            @RequestParam int year, @RequestParam int month) {

        List<MonthlyTaskSummaryDto> calendar = calendarService.getMonthlyCalendar(memberId, year,
                month);
        return ResponseEntity.ok(calendar);
    }

    @GetMapping("/today")
    public ResponseEntity<List<TaskResDto>> getTodayTask(
            @LoginMember Long memberId
    ) {
        List<TaskResDto> todayTasks = calendarService.getTodayTasks(memberId);
        return ResponseEntity.ok(todayTasks);
    }

}
