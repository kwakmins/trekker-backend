package com.trekker.domain.calender.dto.res;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MonthlyTaskSummaryDto(

        LocalDate startDate,

        LocalDate endDate,

        String name
) {

}