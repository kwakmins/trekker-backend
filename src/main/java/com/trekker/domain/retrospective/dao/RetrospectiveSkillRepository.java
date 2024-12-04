package com.trekker.domain.retrospective.dao;

import com.trekker.domain.retrospective.entity.RetrospectiveSkill;
import com.trekker.domain.task.dto.SkillCountDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RetrospectiveSkillRepository extends JpaRepository<RetrospectiveSkill, Long> {

    @Query("""
           SELECT new com.trekker.domain.task.dto.SkillCountDto(rs.skill.name, COUNT(rs.skill.name))
           FROM RetrospectiveSkill rs
           JOIN rs.retrospective r
           JOIN r.task t
           JOIN t.project p
           WHERE p.id = :projectId AND rs.type = :type
           GROUP BY rs.skill.name
           ORDER BY COUNT(rs.skill.name) DESC
           """)
    List<SkillCountDto> findTopSkillsByType(@Param("projectId") Long projectId,
            @Param("type") String type,
            Pageable pageable);

    @Query("""
           SELECT new com.trekker.domain.task.dto.SkillCountDto(rs.skill.name, COUNT(rs.skill.name))
           FROM RetrospectiveSkill rs
           JOIN rs.retrospective r
           JOIN r.task t
           JOIN t.project p
           JOIN p.member m
           WHERE m.id = :memberId AND rs.type = :type
           GROUP BY rs.skill.name
           ORDER BY COUNT(rs.skill.name) DESC
           """)
    List<SkillCountDto> findTopSkillsByMemberIdAndType(@Param("memberId") Long memberId,
            @Param("type") String type,
            Pageable pageable);

    @Query("""
           SELECT new com.trekker.domain.task.dto.SkillCountDto(rs.skill.name, COUNT(rs.skill.name))
           FROM RetrospectiveSkill rs
           JOIN rs.retrospective r
           JOIN r.task t
           JOIN t.project p
           JOIN p.member m
           WHERE m.id = :memberId AND rs.type = :type
           GROUP BY rs.skill.name
           ORDER BY COUNT(rs.skill.name) DESC
           """)
    List<SkillCountDto> findSkillsByMemberIdAndType(@Param("memberId") Long memberId,
            @Param("type") String type);

}
