package com.trekker.domain.report.service;

import com.trekker.domain.report.dto.ReportResDto;
import com.trekker.domain.report.util.ProgressRateUtil;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.dto.SkillCountDto;
import com.trekker.domain.task.entity.Task;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final String SOFT_SKILL = "소프트";
    private static final String HARD_SKILL = "하드";

    private final RetrospectiveSkillRepository retrospectiveSkillRepository;
    private final TaskRepository taskRepository;


    /**
     * 회원의 보고서를 반환합니다
     * @param memberId 회원의 ID
     * @return 상위 소프트/하드 스킬, 이번 달의 날짜별 진행률, 주간 완료된 작업 수를 계산한 데이터를 담는 DTO
     */
    public ReportResDto getMemberReport(Long memberId) {

        // 상위 3개의 소프트 스킬 반환
        List<SkillCountDto> topSoftSkills = retrospectiveSkillRepository.findTopSkillsByMemberIdAndType(
                memberId, SOFT_SKILL, PageRequest.of(0, 3));

        // 상위 3개의 하드 스킬 반환
        List<SkillCountDto> topHardSkills = retrospectiveSkillRepository.findTopSkillsByMemberIdAndType(
                memberId, HARD_SKILL, PageRequest.of(0, 3));

        // 이번 달의 날짜별 작업 통계 계산
        Map<LocalDate, Map<String, Integer>> dailyTaskStatsInMonth = getDailyTaskStatsInMonth(
                memberId);

        // 이번 달의 날짜별 작업 진행률 계산
        Map<LocalDate, Integer> monthlyTaskRate = ProgressRateUtil.calculateProgressRate(
                dailyTaskStatsInMonth);

        // 이번 주의 일별 완료된 작업 수 계산
        Map<LocalDate, Integer> weeklyTaskCounts = getLastWeekToThisSaturdayTasks(
                dailyTaskStatsInMonth);

        return new ReportResDto(topSoftSkills, topHardSkills, monthlyTaskRate, weeklyTaskCounts);
    }

    /**
     * 이번 달의 날짜별 전체 작업 및 완료된 작업 통계를 가져옵니다.
     *
     * @param memberId 멤버 ID
     * @return 날짜별 작업 통계 (전체 작업 수, 완료된 작업 수)
     */
    private Map<LocalDate, Map<String, Integer>> getDailyTaskStatsInMonth(Long memberId) {
        Map<LocalDate, Map<String, Integer>> dailyStats = new HashMap<>();

        // 현재 날짜를 기준으로 이번 달의 시작일과 종료일 계산
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        // 멤버의 이번 달 작업 조회
        List<Task> tasks = taskRepository.findTasksInMonth(memberId, startOfMonth, endOfMonth);

        // 작업의 날짜별 전체 및 완료된 작업 수 계산
        for (Task task : tasks) {
            // 작업 기간 계산
            LocalDate startDate = task.getStartDate();
            LocalDate endDate = (task.getEndDate() != null) ? task.getEndDate() : startDate;

            // 작업 기간을 이번 달 범위 내로 제한
            LocalDate current = startDate.isBefore(startOfMonth) ? startOfMonth : startDate;
            LocalDate end = endDate.isAfter(endOfMonth) ? endOfMonth : endDate;

            // 작업 기간 동안 각 날짜별 작업 수 계산
            while (!current.isAfter(end)) {
                // 작업이 완료된 경우, 완료된 작업 수를 증가시킴
                dailyStats.putIfAbsent(current, new HashMap<>());

                Map<String, Integer> stats = dailyStats.get(current);

                // 전체 작업 수를 증가시킴
                stats.put("totalTasks", stats.getOrDefault("totalTasks", 0) + 1);

                // 작업이 완료된 경우, 완료된 작업 수를 증가시킴
                if (task.getIsCompleted()) {
                    stats.put("completedTasks", stats.getOrDefault("completedTasks", 0) + 1);
                }

                // 다음 날짜로 이동
                current = current.plusDays(1);
            }
        }

        return dailyStats;
    }

    /**
     * 주어진 일별 작업 통계에서 저번 주 일요일부터 이번 주 토요일까지의 작업 데이터를 필터링합니다.
     *
     * @param dailyTaskStats 날짜 별 작업 통계
     * @return 저번 주 일요일부터 이번 주 토요일까지의 날짜별 완료된 작업 수 맵
     */
    private Map<LocalDate, Integer> getLastWeekToThisSaturdayTasks(
            Map<LocalDate, Map<String, Integer>> dailyTaskStats) {
        Map<LocalDate, Integer> weeklyCompletedTasks = new HashMap<>();

        // 현재 날짜를 기준으로 저번 주 일요일과 이번 주 토요일 계산
        LocalDate now = LocalDate.now();
        LocalDate lastSunday = now.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate thisSaturday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        // 주어진 작업 통계에서 해당 주 범위의 데이터를 필터링
        for (Map.Entry<LocalDate, Map<String, Integer>> entry : dailyTaskStats.entrySet()) {
            LocalDate date = entry.getKey();
            if (!date.isBefore(lastSunday) && !date.isAfter(thisSaturday)) {
                int completedTasks = entry.getValue().getOrDefault("completedTasks", 0);
                weeklyCompletedTasks.put(date, completedTasks);
            }
        }

        return weeklyCompletedTasks;
    }

}
