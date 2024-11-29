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
import com.trekker.domain.task.util.TaskStatusDeterminer;
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

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private static final int TASK_RANGE_DAYS = 3;

    @Transactional
    public Long addTask(Long memberId, Long projectId, TaskReqDto taskReqDto) {
        // 회원 및 프로젝트 조회 후 검증
        Project project = findProjectByIdWithMember(projectId);
        project.validateOwner(memberId);

        // 작업 상태 결정 ("하는중", "예정")
        String status = TaskStatusDeterminer.determineStatus(taskReqDto.startDate(),
                taskReqDto.endDate());
        // Entity로 변환
        Task task = taskReqDto.toEntity(project, status);
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
     **/
    public ProjectWithTaskInfoResDto getTaskList(Long memberId, Long projectId, LocalDate reqDate) {
        // 프로젝트 정보 조회
        Project project = findProjectByIdWithMember(projectId);
        project.validateOwner(memberId);

        // +-3일 범위 계산
        LocalDate startDate = reqDate.minusDays(TASK_RANGE_DAYS);
        LocalDate endDate = reqDate.plusDays(TASK_RANGE_DAYS);

        // 태스크 데이터 조회
        List<Task> tasksInRange = taskRepository.findTasksWithinDateRange(projectId, startDate,
                endDate);

        // 선택한 날짜의 태스크 필터링
        List<TaskResDto> tasksOnReqDate = tasksInRange.stream()
                .filter(task -> TaskFilter.isDateWithinRange(reqDate, task.getStartDate(),
                        task.getEndDate()))
                .map(TaskResDto::toDto)
                .collect(Collectors.toList());

        // 날짜별 완료 여부 계산
        List<TaskCompletionStatusResDto> weeklyAchievement = startDate.datesUntil(
                        endDate.plusDays(1))
                .map(date -> {
                    boolean isCompleted = TaskFilter.isTaskCompletedOnDate(tasksInRange, date);
                    return new TaskCompletionStatusResDto(date, isCompleted);
                })
                .collect(Collectors.toList());

        // DTO 생성 및 반환
        return ProjectWithTaskInfoResDto.toDto(project, weeklyAchievement, tasksOnReqDate);
    }

    @Transactional
    public void updateTask(Long memberId, Long taskId, TaskReqDto taskReqDto) {
        // 회원 검증 및 할일 조회
        Task task = taskRepository.findTaskByIdWithProjectAndMember(taskId)
                .orElseThrow(
                        () -> new BusinessException(taskId, "taskId", ErrorCode.TASK_NOT_FOUND)
                );
        task.getProject().validateOwner(memberId);

        // 작업 상태 결정 ("하는중", "예정")
        String status = TaskStatusDeterminer.determineStatus(taskReqDto.startDate(),
                taskReqDto.endDate());

        // 할 일 업데이트
        task.updateTask(taskReqDto, status);
    }

    @Transactional
    public void deleteTask(Long memberId, Long taskId) {
        // 회원 검증 및 할일 조회
        Task task = taskRepository.findTaskByIdWithProjectAndMember(taskId)
                .orElseThrow(
                        () -> new BusinessException(taskId, "taskId", ErrorCode.TASK_NOT_FOUND)
                );
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
}