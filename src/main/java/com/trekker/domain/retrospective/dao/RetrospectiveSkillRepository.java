package com.trekker.domain.retrospective.dao;

import com.trekker.domain.retrospective.entity.RetrospectiveSkill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrospectiveSkillRepository extends JpaRepository<RetrospectiveSkill, Long> {

}
