package com.trekker.domain.retrospective.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "retrospective_skills")
public class RetrospectiveSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "retrospective_skill_seq")
    @SequenceGenerator(
            name = "retrospective_skill_seq",
            sequenceName = "retrospective_skill_sequence",
            allocationSize = 30
    )
    private Long id;

    // 스킬의 유형 (소프트, 하드)
    @Column(name = "type", nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrospective_id", nullable = false)
    private Retrospective retrospective;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Builder
    public RetrospectiveSkill(Long id, String type, Retrospective retrospective, Skill skill) {
        this.id = id;
        this.type = type;
        this.retrospective = retrospective;
        this.skill = skill;
    }

    public static RetrospectiveSkill toEntity(String type, Retrospective retrospective,
            Skill skill) {
        return RetrospectiveSkill.builder()
                .type(type)
                .retrospective(retrospective)
                .skill(skill)
                .build();
    }
}
