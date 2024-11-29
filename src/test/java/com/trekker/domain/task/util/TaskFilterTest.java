package com.trekker.domain.task.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskFilterTest {

    @DisplayName("선택한 날짜의 할 일 목록을 포함시킵니다.. True 일 시 포함, False 일 시 미포함")
    @Test
    void isDateWithinRange() {
        LocalDate reqDate = LocalDate.of(2024, 11, 29);

        // startDate와 reqDate가 같은 경우
        assertThat(
                TaskFilter.isDateWithinRange(reqDate, LocalDate.of(2024, 11, 29), null)).isTrue();

        // reqDate가 범위의 시작 날짜보다 이전
        assertThat(
                TaskFilter.isDateWithinRange(reqDate, LocalDate.of(2024, 11, 30), null)).isFalse();

        // reqDate가 범위의 끝 날짜보다 이후
        assertThat(TaskFilter.isDateWithinRange(reqDate, LocalDate.of(2024, 11, 27),
                LocalDate.of(2024, 11, 28))).isFalse();

        // reqDate가 범위의 시작과 끝 사이에 포함
        assertThat(TaskFilter.isDateWithinRange(reqDate, LocalDate.of(2024, 11, 28),
                LocalDate.of(2024, 11, 30))).isTrue();

        // 범위의 시작 날짜와 끝 날짜가 동일
        assertThat(TaskFilter.isDateWithinRange(reqDate, LocalDate.of(2024, 11, 29),
                LocalDate.of(2024, 11, 29))).isTrue();

        // 끝 날짜가 null
        assertThat(
                TaskFilter.isDateWithinRange(reqDate, LocalDate.of(2024, 11, 29), null)).isTrue();
    }
}

