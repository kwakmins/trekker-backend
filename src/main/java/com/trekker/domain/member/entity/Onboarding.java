package com.trekker.domain.member.entity;

import com.trekker.global.entity.AuditBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "onboardings")
public class Onboarding extends AuditBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "onboarding_id", nullable = false)
    private Long id;

    //온보딩 작성 완료 여부
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Builder
    public Onboarding(Long id, boolean isCompleted) {
        this.id = id;
        this.isCompleted = isCompleted;
    }

    public static Onboarding toOnboarding() {
        return Onboarding.builder()
                .isCompleted(false)
                .build();
    }

    public void updateCompleted() {
        this.isCompleted =true;
    }
}
