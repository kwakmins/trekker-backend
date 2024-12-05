package com.trekker.domain.report.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 날짜별 작업 통계를 기반으로 진행률을 계산하는 유틸리티 클래스입니다.
 * 진행률은 완료된 작업 수를 기준으로 계산되며, 사전 정의된 구간 값으로 반환됩니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressRateCalculator {

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

        // 월별 작업 통계를 스트림으로 처리하여 진행률 맵 생성
        return monthlyTask.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // 날짜를 키로 사용
                        entry -> calculateMappedProgressRate(entry.getValue())
                ));
    }

    /**
     * 일별 작업 통계를 기반으로 매핑된 진행률을 계산합니다.
     *
     * @param dailyStats 일별 작업 통계
     * @return 매핑된 진행률
     */
    private static int calculateMappedProgressRate(Map<String, Integer> dailyStats) {
        // 일별 작업 수 및 완료된 작업 수 가져오기
        int completedTaskCount = dailyStats.getOrDefault("completedTasks", 0);
        int totalTaskCount = dailyStats.getOrDefault("totalTasks", 0);

        // 완료한 작업 수가 0이거나 총 작업 수가 0인 경우 진행률은 0으로 설정
        if (completedTaskCount == 0 || totalTaskCount == 0) {
            return 0;
        }

        // 실제 진행률 계산
        double progressRate = (completedTaskCount / (double) totalTaskCount) * 100;

        // 실제 진행률을 사전 정의된 구간 값으로 매핑
        return mapProgressRate(progressRate);
    }

    /**
     * 실제 진행률을 사전 정의된 구간 값으로 매핑합니다.
     *
     * @param progressRate 실제 계산된 진행률
     * @return 매핑된 진행률
     */
    private static int mapProgressRate(double progressRate) {
        if (progressRate <= 30) {
            return PROGRESS_20;
        } else if (progressRate <= 50) {
            return PROGRESS_40;
        } else if (progressRate <= 70) {
            return PROGRESS_60;
        } else if (progressRate <= 99) {
            return PROGRESS_80;
        } else {
            return PROGRESS_100;
        }
    }

}