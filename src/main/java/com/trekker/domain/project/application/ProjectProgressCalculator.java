package com.trekker.domain.project.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProjectProgressCalculator {
    public static int calculateProgress(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        if (today.isBefore(startDate)) {
            return 0; // 현재 날짜가 시작 날짜 이전
        }

        if (endDate == null) {
            // 종료 날짜가 없는 경우
            long daysElapsed = ChronoUnit.DAYS.between(startDate, today);
            return (int) Math.min((daysElapsed / 100.0) * 100, 100);
        }

        if (today.isAfter(endDate)) {
            return 100; // 현재 날짜가 종료 날짜 이후
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        long elapsedDays = ChronoUnit.DAYS.between(startDate, today);

        return (int) ((double) elapsedDays / totalDays * 100);
    }

}
