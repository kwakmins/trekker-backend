package com.trekker.domain.project.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dto.ProjectReqDto;
import com.trekker.global.entity.BaseEntity;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Builder
    public Project(Long id, String type, String title, String description, LocalDate startDate,
            LocalDate endDate, Boolean isCompleted, Member member) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = isCompleted;
        this.member = member;
    }
    public void validateOwner(Member member) {
        if (!this.member.equals(member)) {
            throw new BusinessException(member.getEmail(), "email", ErrorCode.ACCESS_DENIED_EXCEPTION);
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
