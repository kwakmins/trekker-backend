package com.trekker.domain.task.application;

import com.trekker.domain.project.dao.ProjectRepository;
import com.trekker.domain.project.dto.res.ProjectWithTaskInfoResDto;
import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.dto.req.TaskReqDto;
import com.trekker.domain.task.dto.res.TaskCompletionStatusResDto;
import com.trekker.domain.task.dto.res.TaskResDto;
import com.trekker.domain.task.entity.Task;
import com.trekker.domain.task.util.TaskFilter;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private static final int TASK_RANGE_DAYS = 3;

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public Long addTask(Long memberId, Long projectId, TaskReqDto taskReqDto) {
        // 회원 및 프로젝트 조회 후 검증
        Project project = findProjectByIdWithMember(projectId);
        project.validateOwner(memberId);

        // 할 일 날짜 검증
        validateTaskDatesWithinProject(project, taskReqDto.startDate(), taskReqDto.endDate());


        // Entity로 변환
        Task task = taskReqDto.toEntity(project);
        Task saveTask = taskRepository.save(task);
        return saveTask.getId();
    }

    /**
     * 특정 날짜를 기준으로 프로젝트의 태스크 목록과 완료 상태를 조회하는 메서드.
     *
     * @param memberId  사용자의 id
     * @param projectId 조회할 프로젝트의 Id.
     * @param reqDate   기준 날짜
     * @return ProjectWithTaskInfoResDto - 프로젝트 정보와 함께 기준 날짜의 태스크 목록, ±3일 내 완료 상태를 포함.
     */
    public ProjectWithTaskInfoResDto getTaskList(Long memberId, Long projectId, LocalDate reqDate) {
        // 프로젝트 정보 조회 및 사용자 검증
        Project project = findProjectByIdWithMember(projectId);
        project.validateOwner(memberId);

        // +-3일 범위 계산
        LocalDate startDate = reqDate.minusDays(TASK_RANGE_DAYS);
        LocalDate endDate = reqDate.plusDays(TASK_RANGE_DAYS);

        // 태스크 데이터 조회
        List<Task> tasksInRange = taskRepository.findTasksWithinDateRange(projectId, startDate, endDate);

        // reqDate 에 해당하는 태스크 목록을 필터링.
        List<TaskResDto> tasksOnReqDate = getTasksOnReqDate(tasksInRange, reqDate);
        // 주어진 범위(startDate ~ endDate) 내 날짜별 태스크 완료 상태를 필터링
        List<TaskCompletionStatusResDto> weeklyAchievement = getWeeklyAchievement(tasksInRange, startDate, endDate);

        // DTO 생성 및 반환
        return ProjectWithTaskInfoResDto.toDto(project, weeklyAchievement, tasksOnReqDate);
    }


    @Transactional
    public void updateTask(Long memberId, Long taskId, TaskReqDto taskReqDto) {
        // 회원 검증 및 할일 조회
        Task task = findTaskByIdWithProjectAndMember(taskId);
        task.getProject().validateOwner(memberId);

        // 할 일 날짜 검증
        validateTaskDatesWithinProject(task.getProject(), taskReqDto.startDate(), taskReqDto.endDate());

        // 할 일 업데이트
        task.updateTask(taskReqDto);
    }

    @Transactional
    public void deleteTask(Long memberId, Long taskId) {
        // 회원 검증 및 할일 조회
        Task task = findTaskByIdWithProjectAndMember(taskId);
        task.getProject().validateOwner(memberId);

        // 할 일 삭제
        taskRepository.delete(task);
    }


    /**
     * 프로젝트 ID로 Project를 조회하고 없으면 예외를 발생시킵니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return Project
     */
    private Project findProjectByIdWithMember(Long projectId) {
        return projectRepository.findProjectByIdWIthMember(projectId)
                .orElseThrow(
                        () -> new BusinessException(projectId, "projectId",
                                ErrorCode.PROJECT_NOT_FOUND)
                );
    }

    /**
     * 테스크 ID로 Task를 조회하고 없으면 예외를 발생시킵니다.
     *
     * @param taskId 조회할 테스크의 ID
     * @return Task
     */
    private Task findTaskByIdWithProjectAndMember(Long taskId) {
        return taskRepository.findTaskByIdWithProjectAndMember(taskId)
                .orElseThrow(
                        () -> new BusinessException(taskId, "taskId", ErrorCode.TASK_NOT_FOUND)
                );
    }
    /**
     * 특정 날짜(reqDate)에 해당하는 태스크 목록을 필터링하여 반환합니다.
     *
     * @param tasksInRange 태스크 목록 (범위 내의 태스크)
     * @param reqDate      기준 날짜
     * @return 기준 날짜의 태스크 DTO 목록
     */
    private List<TaskResDto> getTasksOnReqDate(List<Task> tasksInRange, LocalDate reqDate) {
        return tasksInRange.stream()
                .filter(task -> TaskFilter.isDateWithinRange(reqDate, task.getStartDate(), task.getEndDate()))
                .map(TaskResDto::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 범위(startDate ~ endDate) 내 날짜별 태스크 완료 상태를 계산하여 반환합니다.
     *
     * @param tasksInRange 태스크 목록 (범위 내의 태스크)
     * @param startDate    시작 날짜
     * @param endDate      종료 날짜
     * @return 날짜별 완료 상태 DTO 목록
     */
    private List<TaskCompletionStatusResDto> getWeeklyAchievement(List<Task> tasksInRange, LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    boolean isCompleted = TaskFilter.isTaskCompletedOnDate(tasksInRange, date);
                    return new TaskCompletionStatusResDto(date, isCompleted);
                })
                .collect(Collectors.toList());
    }


    /**
     * 작업의 시작일과 종료일이 프로젝트 기간 내에 있는지 검증합니다. 또한 할 일의 종료일이 시작일보다 빠른지를 검증합니다.
     *
     * @param project   작업이 속한 프로젝트
     * @param startDate 작업의 시작일
     * @param endDate   작업의 종료일 (nullable)
     * @throws BusinessException 시작일 또는 종료일이 조건을 만족하지 않을 경우 예외를 발생시킵니다.
     */
    void validateTaskDatesWithinProject(Project project, LocalDate startDate, LocalDate endDate) {
        // startDate 검증: 작업 시작일이 프로젝트 시작일보다 빠른 경우 예외 발생
        if (project.getStartDate().isAfter(startDate)) {
            throw new BusinessException(startDate, "startDate", ErrorCode.TASK_BAD_REQUEST);
        }

        // endDate 검증: 종료일이 null이 아닌 경우 추가 검증 수행
        if (endDate != null) {
            // 종료일이 프로젝트 종료일 이후인 경우 예외 발생
            if (project.getEndDate() != null && endDate.isAfter(project.getEndDate())) {
                throw new BusinessException(endDate, "endDate", ErrorCode.TASK_BAD_REQUEST);
            }
            // 종료일이 시작일보다 빠른 경우 예외 발생
            if (endDate.isBefore(startDate)) {
                throw new BusinessException(endDate, "endDate", ErrorCode.TASK_BAD_REQUEST);
            }
        }
    }


}