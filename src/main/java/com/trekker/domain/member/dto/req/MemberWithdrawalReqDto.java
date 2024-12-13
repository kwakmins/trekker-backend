package com.trekker.domain.member.dto.req;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.member.entity.MemberWithdrawalFeedback;

// 탈퇴 시 필요로 하는 요청 값(피드백)을 담는 DTO
public record MemberWithdrawalReqDto(

        String accessToken,

        String feedback,

        String reason
) {

    public MemberWithdrawalFeedback toEntity(Member member) {
        return MemberWithdrawalFeedback.builder()
                .feedback(feedback)
                .withdrawalReason(reason)
                .member(member)
                .build();
    }
}