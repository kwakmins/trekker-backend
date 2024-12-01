package com.trekker.domain.project.dto.req;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProjectExtendReqDto (
        @NotNull LocalDate endDate
        ) {

}
