package com.trekker.domain.project.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dto.req.ProjectReqDto;
import com.trekker.domain.task.entity.Task;
import com.trekker.global.entity.BaseEntity;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "projects")
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id", nullable = false)
    private Long id;
    // 프로젝트 유형(개인, 팀)
    @Column(nullable = false, length = 10)
    private String type;
    // 프로젝트 제목
    @Column(nullable = false, length = 20)
    private String title;
    // 프로젝트 설명
    @Column(nullable = false, length = 50)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // 프로젝트 완료 여부
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Task> taskList = new ArrayList<>();

    @Builder
    public Project(Long id, String type, String title, String description, LocalDate startDate,
            LocalDate endDate, Boolean isCompleted, Member member, List<Task> taskList) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = isCompleted;
        this.member = member;
        this.taskList = taskList;
    }
    public void validateOwner(Long memberId) {
        if (!this.member.getId().equals(memberId)) {
            throw new BusinessException(member.getId(), "id", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }
    }

    public void updateProject(ProjectReqDto projectReqDto) {
        this.type = projectReqDto.type();
        this.title = projectReqDto.title();
        this.description = projectReqDto.description();
        this.startDate = projectReqDto.startDate();
        this.endDate = projectReqDto.endDate();
    }
}
