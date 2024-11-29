package com.trekker.domain.task.dto.res;

import java.time.LocalDate;
import lombok.Builder;
@Builder
public record TaskCompletionStatusResDto(
        LocalDate date,
        boolean isCompleted
)
{ }