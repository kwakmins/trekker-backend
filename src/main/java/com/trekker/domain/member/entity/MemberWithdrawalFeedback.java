package com.trekker.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member_withdrawal_feedbacks")
public class MemberWithdrawalFeedback {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_withdrawal_feedback_id", nullable = false)
    private Long id;

    @Column(name = "withdrawal_reason", length = 100)
    private String withdrawalReason;

    @Column(name = "feedback", length = 100)
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    @Builder
    public MemberWithdrawalFeedback(Long id, Member member, String withdrawalReason,
            String feedback) {
        this.id = id;
        this.member = member;
        this.withdrawalReason = withdrawalReason;
        this.feedback = feedback;
    }

}
