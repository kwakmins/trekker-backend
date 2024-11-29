package com.trekker.domain.task.entity;

import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.dto.req.TaskReqDto;
import com.trekker.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public Task(Long id, String name, LocalDate start_date, LocalDate end_date, String status,
            Project project) {
        this.id = id;
        this.name = name;
        this.startDate = start_date;
        this.endDate = end_date;
        this.status = status;
        this.project = project;
    }

    public void updateTask(TaskReqDto taskReqDto, String status) {
        this.name = taskReqDto.name();
        this.endDate = taskReqDto.endDate();
        this.status = status;
    }
}