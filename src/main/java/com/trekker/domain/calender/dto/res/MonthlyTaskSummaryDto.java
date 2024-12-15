package com.trekker.domain.calender.dto.res;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MonthlyTaskSummaryDto(
        // 시작일
        LocalDate startDate,
        // 종료일
        LocalDate endDate,
        // 할 일 이름
        String name
) {

}