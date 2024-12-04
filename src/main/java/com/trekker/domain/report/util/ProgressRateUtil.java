package com.trekker.domain.report.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 날짜별 작업 통계를 기반으로 진행률을 계산하는 유틸리티 클래스입니다.
 * 진행률은 완료된 작업 수를 기준으로 계산되며, 사전 정의된 구간 값으로 반환됩니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressRateUtil {

    private static final int PROGRESS_20 = 20;
    private static final int PROGRESS_40 = 40;
    private static final int PROGRESS_60 = 60;
    private static final int PROGRESS_80 = 80;
    private static final int PROGRESS_100 = 100;

    /**
     * 월별 진행률을 계산합니다.
     *
     * @param monthlyTask 날짜별 작업 통계
     * @return 날짜별 진행률
     */
    public static Map<LocalDate, Integer> calculateProgressRate(
            Map<LocalDate, Map<String, Integer>> monthlyTask) {

        Map<LocalDate, Integer> monthlyTaskProgress = new HashMap<>();

        for (Map.Entry<LocalDate, Map<String, Integer>> entry : monthlyTask.entrySet()) {
            LocalDate date = entry.getKey();
            Map<String, Integer> dailyStats = entry.getValue();

            // 작업 개수 가져오기
            Integer totalTaskCount = dailyStats.getOrDefault("totalTasks", 0);
            Integer completedTaskCount = dailyStats.getOrDefault("completedTasks", 0);

            // 완료한 작업 수가 0인 경우 진행률은 0으로 간주
            if (completedTaskCount == 0) {
                monthlyTaskProgress.put(date, 0);
                continue;
            }

            // 진행률 계산
            double progressRate = (completedTaskCount / (double) totalTaskCount) * 100;

            // 조건에 따라 진행률 매핑
            if (progressRate <= 30) {
                monthlyTaskProgress.put(date, PROGRESS_20);
            } else if (progressRate <= 50) {
                monthlyTaskProgress.put(date, PROGRESS_40);
            } else if (progressRate <= 70) {
                monthlyTaskProgress.put(date, PROGRESS_60);
            } else if (progressRate <= 99) {
                monthlyTaskProgress.put(date, PROGRESS_80);
            } else {
                monthlyTaskProgress.put(date, PROGRESS_100);
            }
        }

        return monthlyTaskProgress;
    }
}