package com.trekker.domain.report.service;

import com.trekker.domain.report.dto.ReportResDto;
import com.trekker.domain.report.util.ProgressRateCalculator;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.dto.SkillCountDto;
import com.trekker.domain.task.entity.Task;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final String SOFT_SKILL = "soft";
    private static final String HARD_SKILL = "hard";
    private static final String TOTAL_TASK = "totalTasks";
    private static final String COMPLETED_TASK = "completedTasks";

    private final RetrospectiveSkillRepository retrospectiveSkillRepository;
    private final TaskRepository taskRepository;


    /**
     * 회원의 보고서를 반환합니다
     *
     * @param memberId 회원의 ID
     * @return 상위 소프트/하드 스킬, 이번 달의 날짜별 진행률, 주간 완료된 할 일 수를 계산한 데이터를 담는 DTO
     */
    public ReportResDto getMemberReport(Long memberId) {

        // 상위 3개의 소프트 스킬 반환
        List<SkillCountDto> topSoftSkills = retrospectiveSkillRepository.findTopSkillsByMemberIdAndType(
                memberId, SOFT_SKILL, PageRequest.of(0, 3));

        // 상위 3개의 하드 스킬 반환
        List<SkillCountDto> topHardSkills = retrospectiveSkillRepository.findTopSkillsByMemberIdAndType(
                memberId, HARD_SKILL, PageRequest.of(0, 3));

        // 이번 달의 날짜별 할 일 통계 계산
        Map<LocalDate, Map<String, Integer>> dailyTaskStatsInMonth = getDailyTaskStatsInMonth(
                memberId);

        // 이번 달의 날짜별 할 일 진행률 계산
        Map<LocalDate, Integer> dailyProgressRatesInMonth = ProgressRateCalculator.calculateProgressRate(
                dailyTaskStatsInMonth);

        // 이번 주의 일별 완료된 할 일 수 계산
        Map<LocalDate, Integer> weeklyTaskCounts = getLastWeekToThisSaturdayTasks(
                dailyTaskStatsInMonth);

        return new ReportResDto(topSoftSkills, topHardSkills, dailyProgressRatesInMonth,
                weeklyTaskCounts);
    }

    /**
     * 회원의 스킬을 조회합니다.
     *
     * @param memberId 회원의 ID
     * @param type     조회할 스킬 유형 (소프트, 하드)
     * @return 조회한 스킬 리스트
     */
    public List<SkillCountDto> getMemberSkillList(Long memberId, String type) {
        return retrospectiveSkillRepository.findTopSkillsByMemberIdAndType(memberId, type,
                Pageable.unpaged());
    }

    /**
     * 회원의 할 일 데이터를 기반으로 이번 달의 날짜별 할 일 통계를 반환합니다.
     *
     * @param memberId 회원 ID
     * @return 날짜별 할 일 통계 (전체 할 일 수와 완료된 할 일 수 포함)
     */
    private Map<LocalDate, Map<String, Integer>> getDailyTaskStatsInMonth(Long memberId) {
        // 이번 달의 시작일과 종료일 계산
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        // 회원의 이번 달 할 일 데이터를 조회
        List<Task> tasks = taskRepository.findTasksInMonth(memberId, startOfMonth, endOfMonth);

        // 할 일 데이터를 날짜별로 집계하여 통계를 생성
        return calculateDailyStats(tasks, startOfMonth, endOfMonth);
    }

    /**
     * 할 일 데이터를 날짜별로 집계하여 할 일 통계를 생성합니다.
     *
     * @param tasks        할 일 리스트
     * @param startOfMonth 이번 달의 시작일
     * @param endOfMonth   이번 달의 종료일
     * @return 날짜별 할 일 통계 (전체 할 일 수와 완료된 할 일 수 포함)
     */
    private Map<LocalDate, Map<String, Integer>> calculateDailyStats(
            List<Task> tasks, LocalDate startOfMonth, LocalDate endOfMonth) {

        return tasks.stream()
                .flatMap(task -> {
                    // 종료일이 없을 경우 endDate를 startDate로 설정
                    LocalDate endDate =
                            (task.getEndDate() != null) ? task.getEndDate() : task.getStartDate();

                    // 할 일 기간을 이번 달의 범위로 조정
                    LocalDate currentStart =
                            task.getStartDate().isBefore(startOfMonth) ? startOfMonth
                                    : task.getStartDate();
                    LocalDate currentEnd = endDate.isAfter(endOfMonth) ? endOfMonth : endDate;

                    // 날짜 범위 스트림 생성
                    long daysBetween = ChronoUnit.DAYS.between(currentStart, currentEnd) + 1;
                    return (daysBetween > 0) ?
                            getDateRangeStream(currentStart, currentEnd).map(
                                    date -> Map.entry(date, task))
                            : Stream.empty();
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, // 날짜별로 그룹화
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                entries -> {
                                    Map<String, Integer> stats = new HashMap<>();
                                    stats.put(TOTAL_TASK, entries.size()); // 전체 할 일 수
                                    stats.put(COMPLETED_TASK,
                                            (int) entries.stream()
                                                    .filter(entry -> entry.getValue()
                                                            .getIsCompleted())
                                                    .count()); // 완료된 할 일 수
                                    return stats;
                                }
                        )
                ));
    }

    /**
     * 주어진 시작일과 종료일 사이의 날짜 스트림을 생성합니다.
     *
     * @param start 시작일
     * @param end   종료일
     * @return 시작일부터 종료일까지의 LocalDate 스트림
     */
    private Stream<LocalDate> getDateRangeStream(LocalDate start, LocalDate end) {
        long daysBetween = ChronoUnit.DAYS.between(start, end) + 1;
        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(daysBetween);
    }



    /**
     * 주어진 일별 할 일 통계에서 저번 주 일요일부터 이번 주 토요일까지의 할 일 데이터를 필터링합니다.
     *
     * @param dailyTaskStats 날짜 별 할 일 통계
     * @return 저번 주 일요일부터 이번 주 토요일까지의 날짜별 완료된 할 일 수 맵
     */
    private Map<LocalDate, Integer> getLastWeekToThisSaturdayTasks(
            Map<LocalDate, Map<String, Integer>> dailyTaskStats) {

        // 현재 날짜를 기준으로 저번 주 일요일과 이번 주 토요일 계산
        LocalDate now = LocalDate.now();
        LocalDate lastSunday = now.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate thisSaturday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        // 주어진 할 일 통계에서 해당 주 범위의 데이터를 필터링
        return dailyTaskStats.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(lastSunday) && !entry.getKey()
                        .isAfter(thisSaturday))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getOrDefault(COMPLETED_TASK, 0)
                ));
    }

}
