package com.trekker.domain.calender.api;

import com.trekker.domain.calender.application.CalenderService;
import com.trekker.domain.calender.dto.res.MonthlyTaskSummaryDto;
import com.trekker.global.config.security.annotation.LoginMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calender")
@RequiredArgsConstructor
public class CalendarController {

    private final CalenderService calenderService;

    @GetMapping("/month")
    public ResponseEntity<List<MonthlyTaskSummaryDto>> getMonthlyCalendar(
            @LoginMember Long memberId,
            @RequestParam int year, @RequestParam int month) {

        List<MonthlyTaskSummaryDto> calendar = calenderService.getMonthlyCalender(memberId, year,
                month);
        return ResponseEntity.ok(calendar);
    }

}
