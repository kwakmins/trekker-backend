package com.trekker.domain.task.dao;

import com.trekker.domain.task.entity.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
           SELECT t
           FROM Task t
           JOIN FETCH t.project p
           JOIN FETCH p.member m
           WHERE t.id =:taskId
           """)
    Optional<Task> findTaskByIdWithProjectAndMember(@Param("taskId") Long taskId);

    @Query("""
           SELECT t
           FROM Task t
           JOIN FETCH t.project
           WHERE t.project.id = :projectId
           AND (
               (t.endDate IS NULL AND t.startDate BETWEEN :startDate AND :endDate) OR
               (t.endDate IS NOT NULL AND t.startDate <= :endDate AND t.endDate >= :startDate)
               )
           """)
    List<Task> findTasksWithinDateRange(@Param("projectId") Long projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}