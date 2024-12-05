package com.trekker.domain.project.dao;

import com.trekker.domain.project.dto.res.ProjectWithTaskCompletedList;
import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.dto.TaskRetrospectiveSkillDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
            SELECT p
            FROM Project p
            JOIN FETCH p.member m
            WHERE p.id =:projectId
            """)
    Optional<Project> findProjectByIdWIthMember(@Param("projectId") Long projectId);

    @Query("""
                SELECT p
                FROM Project p
                WHERE p.member.id = :memberId
                AND p.isCompleted = false
                AND (:type IS NULL OR p.type = :type)
            """)
    List<Project> findFilteredProjects(@Param("memberId") Long memberId,
            @Param("type") String type);

    @Query("""
            SELECT new com.trekker.domain.project.dto.res.ProjectWithTaskCompletedList(
                 p.id,
                 p.title,
                 COUNT(t.id)
            )
            FROM Project p
            JOIN p.taskList t
            WHERE p.member.id = :memberId AND t.isCompleted = true
            """)
    List<ProjectWithTaskCompletedList> findProjectWithTaskCompleted(
            @Param("memberId") Long memberId);


    @Query("""
           SELECT new com.trekker.domain.task.dto.TaskRetrospectiveSkillDto(
               t.id,
               t.startDate,
               t.endDate,
               r.content,
               rs.type,
               s.name
           )
           FROM Task t
           JOIN t.project p
           JOIN p.member m
           JOIN t.retrospective r
           LEFT JOIN r.retrospectiveSkillList rs
           LEFT JOIN rs.skill s
           WHERE p.id = :projectId AND m.id = :memberId
           """)
    List<TaskRetrospectiveSkillDto> findTaskRetrospectivesByProjectIdAndMemberId(
            @Param("projectId") Long projectId,
            @Param("memberId") Long memberId);
}
