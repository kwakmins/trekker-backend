package com.trekker.domain.member.dao;

import com.trekker.domain.member.entity.MemberFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberFeedbackRepository extends JpaRepository<MemberFeedback, Long>{

}
