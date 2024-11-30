package com.trekker.domain.retrospective.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.trekker.domain.task.entity.Task;
import com.trekker.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "retrospectives")
public class Retrospective extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "retrospective_id", nullable = false)
    private Long id;

    // 회고의 내용
    @Column(name = "content")
    private String content;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @OneToMany(mappedBy = "retrospective", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RetrospectiveSkill> retrospectiveSkillList = new ArrayList<>();

    @Builder
    public Retrospective(Long id, String content, Task task, List<RetrospectiveSkill> retrospectiveSkillList) {
        this.id = id;
        this.content = content;
        this.task = task;
        this.retrospectiveSkillList = retrospectiveSkillList;
    }

    public void updateContent(String content) {
        this.content =content;
    }
}
