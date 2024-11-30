package com.trekker.domain.retrospective.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id", nullable = false)
    private Long id;

    // 스킬 이름
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "skill", orphanRemoval = true)
    private List<RetrospectiveSkill> retrospectiveSkillList = new ArrayList<>();

    @Builder
    public Skill(Long id, String name, List<RetrospectiveSkill> retrospectiveSkillList) {
        this.id = id;
        this.name = name;
        this.retrospectiveSkillList = retrospectiveSkillList;
    }

    public static Skill toEntity(String name) {
        return Skill.builder()
                .name(name)
                .build();
    }
}
