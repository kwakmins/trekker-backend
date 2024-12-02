package com.trekker.domain.retrospective.dao;

import com.trekker.domain.retrospective.entity.Skill;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // 이름 목록에 해당하는 Skill을 연관 데이터를 패치 조인으로 조회
    @Query("""
           SELECT s
           FROM Skill s
           WHERE s.name IN :names
           """)
    List<Skill> findByNameIn(@Param("names") Collection<String> names);

}
