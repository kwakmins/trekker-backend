package com.trekker.domain.project.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.trekker.domain.task.entity.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "project_retrospectives")
public class ProjectRetrospective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id", nullable = false)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "content", nullable = false)
    private String content;

    @Builder
    public ProjectRetrospective(Long id, Project project, String content) {
        this.id = id;
        this.project = project;
        this.content = content;
    }
}
