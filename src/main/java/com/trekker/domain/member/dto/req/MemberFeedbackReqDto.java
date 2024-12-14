package com.trekker.domain.member.dto.req;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.member.entity.MemberFeedback;
import jakarta.validation.constraints.Size;

public record MemberFeedbackReqDto(
        @Size(max = 100)
        String content
) {

    public MemberFeedback toEntity(Member member) {
        return MemberFeedback.builder()
                .content(content)
                .member(member)
                .build();
    }

}
