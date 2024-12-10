package com.trekker.domain.report.dto;

import com.trekker.domain.task.dto.SkillCountDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ReportResDto(

        List<SkillCountDto> softSkillList,

        List<SkillCountDto> hardSkillList,

        Map<LocalDate, Integer> dailyProgressRatesInMonth,

        Map<LocalDate, Integer> weeklyCompletedTasks
) {

}