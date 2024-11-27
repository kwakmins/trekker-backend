package com.trekker.domain.project.dao;

import com.trekker.domain.project.entity.Project;
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
}
