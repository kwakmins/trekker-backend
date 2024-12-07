package com.trekker.domain.report.dto;

import com.trekker.domain.task.dto.SkillCountDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ReportResDto(

        @Schema(description = "회원 소프트 스킬 목록")
        List<SkillCountDto> softSkillList,

        @Schema(description = "회원 하드 스킬 목록")
        List<SkillCountDto> hardSkillList,

        @Schema(description = "월 별 진행률 (날짜와 진행률 매핑)",
                example = """
                {
                  "2024-01-01": 40,
                  "2024-01-02": 60,
                  "2024-01-03": 80
                }
                          """)
        Map<LocalDate, Integer> dailyProgressRatesInMonth,

        @Schema(description = "주간 작업 완료 횟수 (주 시작 날짜와 완료 횟수 매핑)",
                example = """
                {
                  "2024-01-01": 5,
                  "2024-01-02": 7,
                  "2024-01-03": 4
                }
                         """)
        Map<LocalDate, Integer> weeklyCompletedTasks
) {

}