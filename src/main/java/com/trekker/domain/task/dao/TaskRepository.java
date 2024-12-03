package com.trekker.domain.task.dao;

import com.trekker.domain.calender.dto.res.MonthlyTaskSummaryDto;
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
           LEFT JOIN FETCH t.retrospective
           WHERE t.project.id = :projectId
           AND (
               (t.endDate IS NULL AND t.startDate BETWEEN :startDate AND :endDate) OR
               (t.endDate IS NOT NULL AND t.startDate <= :endDate AND t.endDate >= :startDate)
               )
           """)
    List<Task> findTasksWithinDateRange(@Param("projectId") Long projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
           SELECT t
           FROM Task t
           JOIN FETCH t.project p
           LEFT JOIN fetch t.retrospective r
           JOIN FETCH p.member m
           WHERE t.id =:taskId
           """)
    Optional<Task> findTaskByIdWithProjectAndMemberWithRetrospective(@Param("taskId") Long taskId);

    @Query("""
           SELECT new com.trekker.domain.calender.dto.res.MonthlyTaskSummaryDto(t.startDate, t.endDate, t.name)
           FROM Task t
           JOIN t.project p
           JOIN p.member m
           WHERE m.id = :memberId
           AND t.endDate IS NOT NULL
           AND (
               (t.startDate BETWEEN :startOfMonth AND :endOfMonth) OR
               (t.endDate BETWEEN :startOfMonth AND :endOfMonth) OR
               (t.startDate <= :startOfMonth AND t.endDate >= :endOfMonth)
               )
           """)
    List<MonthlyTaskSummaryDto> getMonthlyTaskSummary(
            @Param("memberId") Long memberId,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth);

    @Query("""
           SELECT t
           FROM Task t
           JOIN FETCH t.project p
           JOIN FETCH p.member m
           LEFT JOIN FETCH t.retrospective
           WHERE m.id =:memberId
           AND (
               (t.endDate IS NULL AND t.startDate =:today) OR
               (t.endDate IS NOT NULL AND t.startDate <=:today AND t.endDate >=:today)
               )
       """)
    List<Task> findTasksForToday(@Param("memberId") Long memberId, @Param("today") LocalDate today);
}