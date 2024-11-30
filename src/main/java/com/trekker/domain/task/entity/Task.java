package com.trekker.domain.task.entity;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.retrospective.entity.Retrospective;
import com.trekker.domain.task.dto.req.TaskReqDto;
import com.trekker.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "tasks")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false)
    private Long id;
    // 할 일 내용
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToOne(mappedBy = "task", fetch = FetchType.LAZY,  cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Retrospective retrospective;

    @Builder
    public Task(Long id, String name, LocalDate start_date, LocalDate end_date, Boolean isCompleted,
            Project project, Retrospective retrospective) {
        this.id = id;
        this.name = name;
        this.startDate = start_date;
        this.endDate = end_date;
        this.isCompleted = isCompleted;
        this.project = project;
        this.retrospective = retrospective;
    }

    public void updateTask(TaskReqDto taskReqDto) {
        this.name = taskReqDto.name();
        this.endDate = taskReqDto.endDate();
    }

    public void updateCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}