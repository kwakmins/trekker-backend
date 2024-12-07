package com.trekker.domain.member.dto.req;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.member.entity.MemberWithdrawalFeedback;
import io.swagger.v3.oas.annotations.media.Schema;

// 탈퇴 시 필요로 하는 요청 값(피드백)을 담는 DTO
public record MemberWithdrawalReqDto(

        @Schema(description = "회원 탈퇴 피드백", example = "사용자 인터페이스가 불편했어요.")
        String feedback,

        @Schema(description = "회원 탈퇴 사유", example = "서비스 사용 빈도 감소")
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