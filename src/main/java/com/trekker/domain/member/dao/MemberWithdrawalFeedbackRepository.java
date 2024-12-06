package com.trekker.domain.member.dao;

import com.trekker.domain.member.entity.MemberWithdrawalFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberWithdrawalFeedbackRepository extends
        JpaRepository<MemberWithdrawalFeedback, Long> {

}
