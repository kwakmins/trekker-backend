package com.trekker.domain.task.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dao.ProjectRepository;
import com.trekker.domain.project.dto.res.ProjectWithTaskInfoResDto;
import com.trekker.domain.project.entity.Project;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.dto.req.TaskReqDto;
import com.trekker.domain.task.entity.Task;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    private Project project;
    private Member member;
    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .build();
        project = Project.builder()
                .id(1L)
                .member(member)
                .startDate(LocalDate.now())
                .build();
    }
    @DisplayName("새로운 할 일을 추가한다.")
    @Test
    void addTask() {
        // given
        TaskReqDto mockDto = mock(TaskReqDto.class);
        Task task = Task.builder()
                .id(1L)
                .build();

        when(mockDto.startDate()).thenReturn(LocalDate.now());
        when(taskRepository.save(any())).thenReturn(task);
        when(projectRepository.findProjectByIdWIthMember(project.getId())).thenReturn(
                Optional.of(project));

        // when
        Long taskId = taskService.addTask(member.getId(), project.getId(), mockDto);

        // then
        assertThat(taskId).isEqualTo(task.getId());
    }
    @DisplayName("할 일 생성시 프로젝트 시작 날짜 이전에 등록될 경우 오류를 발생한다.")
    @Test
    void addTaskFailDueToInvalidDates_1() {
        //given
        TaskReqDto taskReqDto = TaskReqDto.builder()
                .startDate(LocalDate.now().minusDays(1))
                .build();

        when(projectRepository.findProjectByIdWIthMember(project.getId())).thenReturn(
                Optional.of(project));

        //when & then
        assertThatThrownBy(() -> taskService.addTask(member.getId(), project.getId(), taskReqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.TASK_BAD_REQUEST.getMessage());
    }
    @DisplayName("할 일 생성시 종료 날짜가 시작 날짜 이전에 등록될 경우 오류를 발생한다.")
    @Test
    void addTaskFailDueToInvalidDates_2() {
        //given
        TaskReqDto taskReqDto = TaskReqDto.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().minusDays(1))
                .build();

        when(projectRepository.findProjectByIdWIthMember(project.getId())).thenReturn(
                Optional.of(project));

        //when & then
        assertThatThrownBy(() -> taskService.addTask(member.getId(), project.getId(), taskReqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.TASK_BAD_REQUEST.getMessage());
    }

    @DisplayName("특정 날찌의 할 일을 전체 조회한다.")
    @Test
    void getTaskList() {
        // given
        Task task = Task.builder()
                .id(1L)
                .start_date(LocalDate.now())
                .build();

        when(projectRepository.findProjectByIdWIthMember(project.getId())).thenReturn(
                Optional.of(project));
        when(taskRepository.findTasksWithinDateRange(eq(project.getId()), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(task));

        // when
        ProjectWithTaskInfoResDto result = taskService.getTaskList(member.getId(), project.getId(), LocalDate.now());

        // then
        assertThat(result.taskList().size()).isEqualTo(1);
        assertThat(result.taskList().get(0).name()).isEqualTo(task.getName());
        verify(taskRepository, times(1)).findTasksWithinDateRange(eq(project.getId()), any(LocalDate.class), any(LocalDate.class));
    }
    @DisplayName("할 일을 업데이트 한다.")
    @Test
    void updateTask() {
        // given
        TaskReqDto taskReqDto = TaskReqDto.builder()
                .name("수정 할 일")
                .startDate(LocalDate.now())
                .build();
        Task task = Task.builder()
                .id(1L)
                .name("할 일")
                .project(project)
                .start_date(LocalDate.now())
                .build();

        when(taskRepository.findTaskByIdWithProjectAndMember(task.getId())).thenReturn(
                Optional.of(task));

        // when
        taskService.updateTask(member.getId(), task.getId(), taskReqDto);

        // then
        assertThat(task.getName()).isEqualTo(taskReqDto.name());
        assertThat(task.getStatus()).isEqualTo("하는중");
    }
    @DisplayName("할 일을 삭제한다.")
    @Test
    void deleteTask() {
        // given
        Task task = Task.builder()
                .id(1L)
                .name("할 일")
                .project(project)
                .start_date(LocalDate.now())
                .build();

        when(taskRepository.findTaskByIdWithProjectAndMember(task.getId())).thenReturn(
                Optional.of(task));

        // when
        taskService.deleteTask(member.getId(), task.getId());

        // then
        verify(taskRepository, times(1)).delete(task);

    }
}