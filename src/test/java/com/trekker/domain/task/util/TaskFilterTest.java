package com.trekker.domain.task.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TaskFilterTest {

    @DisplayName("주어진 날짜가 범위 내에 포함되는지 확인")
    @ParameterizedTest(name = "reqDate={0}, startDate={1}, endDate={2}, expected={3}")
    @MethodSource("provideDateRangeTestCases")
    void isDateWithinRange(LocalDate reqDate, LocalDate startDate, LocalDate endDate,
            boolean expected) {
        // when
        boolean result = TaskFilter.isDateWithinRange(reqDate, startDate, endDate);

        // then
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> provideDateRangeTestCases() {
        return Stream.of(
                // startDate와 reqDate가 같은 경우
                Arguments.of(LocalDate.of(2024, 11, 29), LocalDate.of(2024, 11, 29), null, true),
                // reqDate가 범위의 시작 날짜보다 이전
                Arguments.of(LocalDate.of(2024, 11, 29), LocalDate.of(2024, 11, 30), null, false),
                // reqDate가 범위의 끝 날짜보다 이후
                Arguments.of(LocalDate.of(2024, 11, 29), LocalDate.of(2024, 11, 27),
                        LocalDate.of(2024, 11, 28), false),
                // reqDate가 범위의 시작과 끝 사이에 포함
                Arguments.of(LocalDate.of(2024, 11, 29), LocalDate.of(2024, 11, 28),
                        LocalDate.of(2024, 11, 30), true),
                // 범위의 시작 날짜와 끝 날짜가 동일
                Arguments.of(LocalDate.of(2024, 11, 29), LocalDate.of(2024, 11, 29),
                        LocalDate.of(2024, 11, 29), true),
                // 끝 날짜가 null
                Arguments.of(LocalDate.of(2024, 11, 29), LocalDate.of(2024, 11, 29), null, true)
        );
    }
}