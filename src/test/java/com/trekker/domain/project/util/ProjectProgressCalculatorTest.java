package com.trekker.domain.project.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProjectProgressCalculator 클래스의 진행률 계산 메서드를 테스트하는 클래스입니다.
 */
class ProjectProgressCalculatorTest {

    @Test
    @DisplayName("시작일 이전일 경우 진행률은 0%")
    void testProgressBeforeStartDate() {
        // Given: 프로젝트 시작일, 종료일, 현재 날짜 설정
        LocalDate startDate = LocalDate.of(2024, 1, 1);  // 프로젝트 시작일
        LocalDate endDate = LocalDate.of(2024, 12, 31);  // 프로젝트 종료일
        LocalDate today = LocalDate.of(2023, 12, 31);    // 현재 날짜 (시작일 이전)

        // When: 진행률 계산
        int progress = ProjectProgressCalculator.calculateProjectProgress(startDate, endDate, today);

        // Then: 진행률은 0%이어야 함
        assertThat(progress).isEqualTo(0);
    }

    @Test
    @DisplayName("종료일 이후일 경우 진행률은 100%")
    void testProgressAfterEndDate() {
        // given: 프로젝트 시작일, 종료일, 현재 날짜 설정
        LocalDate startDate = LocalDate.of(2024, 1, 1);  // 프로젝트 시작일
        LocalDate endDate = LocalDate.of(2024, 12, 31);  // 프로젝트 종료일
        LocalDate today = LocalDate.of(2025, 1, 1);      // 현재 날짜 (종료일 이후)

        // when: 진행률 계산
        int progress = ProjectProgressCalculator.calculateProjectProgress(startDate, endDate, today);

        // then: 진행률은 100%이어야 함
        assertThat(progress).isEqualTo(100);
    }

    @Test
    @DisplayName("시작일과 종료일 사이의 날짜에 대한 진행률 계산")
    void testProgressWithinRange() {
        // given
        // 프로젝트 시작일, 종료일, 현재 날짜 설정
        LocalDate startDate = LocalDate.of(2024, 1, 1);  // 프로젝트 시작일
        LocalDate endDate = LocalDate.of(2024, 12, 31);  // 프로젝트 종료일
        LocalDate today = LocalDate.of(2024, 6, 1);      // 현재 날짜 (진행 중)

        // when
        // 진행률 계산
        int progress = ProjectProgressCalculator.calculateProjectProgress(startDate, endDate, today);

        // then
        // 진행률은 41%이어야 함 (151/366 = 약 41.2%)
        assertThat(progress).isEqualTo(41);
    }
}