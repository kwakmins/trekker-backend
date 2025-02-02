package com.trekker.domain.member.dto.res;

import com.trekker.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberResDto(

        String name,

        String jobName,

        String provider,

        String profileImage
) {

    public static MemberResDto toDto(Member member) {
        return MemberResDto.builder()
                .name(member.getName())
                .jobName(member.getJob().getJobName())
                .provider(member.getSocialProvider().getProvider())
                .profileImage(member.getProfileImage())
                .build();
    }
}