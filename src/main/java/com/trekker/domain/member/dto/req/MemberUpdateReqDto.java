package com.trekker.domain.member.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MemberUpdateReqDto(
        @NotNull
        String name,
        @NotNull
        String jobName
) {

}
