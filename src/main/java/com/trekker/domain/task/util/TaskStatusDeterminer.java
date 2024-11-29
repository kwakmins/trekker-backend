package com.trekker.domain.task.util;

import com.trekker.global.exception.custom.BusinessException;
import java.time.LocalDate;
/**
 * 작업 상태를 결정하는 유틸리티 클래스.
 * 주어진 시작 날짜와 종료 날짜를 기준으로 작업의 상태를 계산합니다.
 */
public final class TaskStatusDeterminer {

    private TaskStatusDeterminer() {
        // 인스턴스화 방지
    }

    /**
     * 시작 날짜와 종료 날짜를 기준으로 작업의 상태를 결정합니다.
     *
     * @param startDate 작업이 시작되는 날짜
     * @param endDate   작업이 종료되는 날짜 (nullable)
     * @return 작업 상태: - "하는중" : 오늘 날짜가 시작 날짜와 종료 날짜 사이에 있는 경우 - "예정" : 시작 날짜가 오늘 이후인 경우
     * @throws BusinessException 시작 날짜가 오늘 이전이거나, 종료 날짜가 시작 날짜보다 이전인 경우
     */
    public static String determineStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        // 상태 결정
        if (startDate.isEqual(today)) {
            return "하는중";
        } else if (endDate != null && (today.isAfter(startDate) && today.isBefore(
                endDate.plusDays(1)))) {
            return "하는중";
        } else { // startDate.isAfter(today)
            return "예정";
        }
    }
}