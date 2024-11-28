package com.trekker.domain.project.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 프로젝트 진행률을 계산하는 유틸리티 클래스입니다.
 * 시작일과 종료일을 기준으로 진행률(%)을 계산합니다.
 */
public final class ProjectProgressCalculator {

    private ProjectProgressCalculator() {
        // 인스턴스화를 방지
    }

    /**
     * 프로젝트 진행률을 계산합니다.
     *
     * @param startDate 프로젝트 시작 날짜
     * @param endDate   프로젝트 종료 날짜 (null일 경우 무기한 진행으로 간주)
     * @param today     오늘 날짜
     * @return 진행률(0 ~ 100 %)을 나타내는 정수 값
     */
    public static int calculateProjectProgress(LocalDate startDate, LocalDate endDate,
            LocalDate today) {
        if (isBeforeStartDate(startDate, today)) {
            return 0; // 시작 날짜 이전
        }

        if (endDate == null) {
            return calculateInfiniteProgress(startDate, today); // 종료 날짜 설정 안한 경우
        }

        if (isAfterEndDate(endDate, today)) {
            return 100; // 종료 날짜 이후
        }

        return calculateBetweenDatesProgress(startDate, endDate, today); //
    }

    /**
     * 현재 날짜가 시작 날짜 이전인지 확인합니다.
     */
    private static boolean isBeforeStartDate(LocalDate startDate, LocalDate today) {
        return today.isBefore(startDate);
    }

    /**
     * 현재 날짜가 종료 날짜 이후인지 확인합니다.
     */
    private static boolean isAfterEndDate(LocalDate endDate, LocalDate today) {
        return today.isAfter(endDate);
    }

    /**
     * 무기한 진행률을 계산합니다.
     */
    private static int calculateInfiniteProgress(LocalDate startDate, LocalDate today) {
        // 경과된 기간: 시작일(startDate)과 오늘(today) 사이의 경과 일수를 계산합니다.
        long daysElapsed = ChronoUnit.DAYS.between(startDate, today);

        return (int) Math.min((daysElapsed / 100.0) * 100, 100);
    }

    /**
     * 특정 기간 내 진행률을 계산합니다.
     */
    private static int calculateBetweenDatesProgress(LocalDate startDate, LocalDate endDate,
            LocalDate today) {

        // 총 기간: 시작일(startDate)과 종료일(endDate) 사이의 총 일수를 계산합니다.
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

        // 경과된 기간: 시작일(startDate)과 오늘(today) 사이의 경과 일수를 계산합니다.
        long elapsedDays = ChronoUnit.DAYS.between(startDate, today);

        return (int) ((double) elapsedDays / totalDays * 100);
    }
}