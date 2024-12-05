package com.trekker.domain.retrospective.dao;

import com.trekker.domain.retrospective.dto.res.SkillDetailResDto;
import com.trekker.domain.retrospective.dto.res.SkillSummaryResDto;
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

    @Query("""
            SELECT new com.trekker.domain.retrospective.dto.res.SkillSummaryResDto(
                s.id,
                s.name,
                COUNT(rs.skill.id)
            )
            FROM Skill s
            JOIN RetrospectiveSkill rs ON s.id = rs.skill.id
            JOIN rs.retrospective r
            JOIN r.task t
            JOIN t.project p
            WHERE p.member.id = :memberId
            GROUP BY s.id, s.name
    """)
    List<SkillSummaryResDto> findAllSkillsWithCountByMemberId(@Param("memberId") Long memberId);

    @Query("""
           SELECT new com.trekker.domain.retrospective.dto.res.SkillDetailResDto(
               t.startDate,
               t.endDate,
               t.name,
               r.content
           )
           FROM Skill s
           JOIN RetrospectiveSkill rs ON s.id = rs.skill.id
           JOIN rs.retrospective r
           JOIN r.task t
           JOIN t.project p
           WHERE s.id = :skillId AND p.member.id = :memberId
           """)
    List<SkillDetailResDto> findSkillDetailsBySkillIdAndMemberId(
            @Param("skillId") Long skillId,
            @Param("memberId") Long memberId);
}
