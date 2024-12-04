package com.trekker.domain.report.dto;

import com.trekker.domain.task.dto.SkillCountDto;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ReportResDto(
        List<SkillCountDto> softSkillList,
        List<SkillCountDto> hardSkillList,

        //월 별 진행률
        Map<LocalDate, Integer> monthlyTaskRate,
        // 주간 작업 완료 횟수
        Map<LocalDate, Integer> weeklyCompletedTasks
) {

}
