package com.trekker.domain.report.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProgressRateCalculatorTest {

    @DisplayName("월별 작업 진행률 계산 테스트")
    @ParameterizedTest(name = "date={0}, totalTasks={1}, completedTasks={2}, expectedProgress={3}")
    @MethodSource("provideProgressRateTestCases")
    void calculateProgressRate(LocalDate date, int totalTasks, int completedTasks, int expectedProgress) {
        // given
        Map<LocalDate, Map<String, Integer>> input = Map.of(
                date, Map.of(
                        "totalTasks", totalTasks,
                        "completedTasks", completedTasks
                )
        );

        // when
        Map<LocalDate, Integer> result = ProgressRateCalculator.calculateProgressRate(input);

        // then
        assertThat(result).containsEntry(date, expectedProgress);
    }

    static Stream<Arguments> provideProgressRateTestCases() {
        return Stream.of(
                // 완료된 작업이 없는 경우
                Arguments.of(LocalDate.of(2024, 12, 1), 10, 0, 0),
                // 진행률이 30 이하인 경우
                Arguments.of(LocalDate.of(2024, 12, 2), 10, 3, 20),
                // 진행률이 50 이하인 경우
                Arguments.of(LocalDate.of(2024, 12, 3), 10, 5, 40),
                // 진행률이 70 이하인 경우
                Arguments.of(LocalDate.of(2024, 12, 4), 10, 7, 60),
                // 진행률이 99 이하인 경우
                Arguments.of(LocalDate.of(2024, 12, 5), 10, 9, 80),
                // 진행률이 100인 경우
                Arguments.of(LocalDate.of(2024, 12, 6), 10, 10, 100)
        );
    }
}