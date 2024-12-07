package com.trekker.domain.member.dto.res;

import com.trekker.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MemberResDto(

        @Schema(description = "회원 이름", example = "홍길동")
        String name,

        @Schema(description = "회원 직업 이름", example = "백엔드 개발자")
        String jobName,

        @Schema(description = "프로필 이미지 경로", example = "/uploads/profile-images/profile123.jpg")
        String profileImage
) {

    public static MemberResDto toDto(Member member) {
        return MemberResDto.builder()
                .name(member.getName())
                .jobName(member.getJob().getJobName())
                .profileImage(member.getProfileImage())
                .build();
    }
}