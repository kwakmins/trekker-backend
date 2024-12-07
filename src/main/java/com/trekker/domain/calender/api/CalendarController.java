package com.trekker.domain.calender.api;

import com.trekker.domain.calender.application.CalendarService;
import com.trekker.domain.calender.dto.res.MonthlyTaskSummaryDto;
import com.trekker.domain.task.dto.res.TaskResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "캘린더 관련 API")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/month")
    @Operation(
            summary = "월간 캘린더 데이터 조회",
            description = "회원의 월간 캘린더 데이터를 조회합니다. 연도와 월을 기준으로 시작일과 종료일을 계산하여 데이터를 반환합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월간 데이터 조회 성공")
    })
    public ResponseEntity<List<MonthlyTaskSummaryDto>> getMonthlyCalendar(
            @Parameter(description = "회원 ID", example = "1") @LoginMember Long memberId,
            @Parameter(description = "연도 (YYYY 형식)", example = "2024") @RequestParam int year,
            @Parameter(description = "월 (1-12)", example = "12") @RequestParam int month
    ) {
        List<MonthlyTaskSummaryDto> calendar = calendarService.getMonthlyCalendar(memberId, year,
                month);
        return ResponseEntity.ok(calendar);
    }

    @GetMapping("/today")
    @Operation(
            summary = "오늘의 할 일 조회",
            description = "회원의 오늘 날짜 기준 할 일 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "오늘의 할 일 조회 성공")
    })
    public ResponseEntity<List<TaskResDto>> getTodayTask(
            @Parameter(description = "회원 ID", example = "1") @LoginMember Long memberId
    ) {
        List<TaskResDto> todayTasks = calendarService.getTodayTasks(memberId);
        return ResponseEntity.ok(todayTasks);
    }

}