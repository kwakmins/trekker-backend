package com.trekker.domain.member.dto.req;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record MemberUpdateReqDto(
        @NotNull
        String name,
        @NotNull
        String jobName,
        // 프로필 이미지
        MultipartFile profileImage
) {

}
