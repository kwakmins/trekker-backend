package com.trekker.domain.project.dao;

import com.trekker.domain.project.dto.res.ProjectWithTaskCompletedList;
import com.trekker.domain.project.entity.Project;
import java.util.List;
import java.util.Optional;
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
            GROUP BY p.id, p.title
            """)
    List<ProjectWithTaskCompletedList> findProjectWithTaskCompleted(
            @Param("memberId") Long memberId);


}
