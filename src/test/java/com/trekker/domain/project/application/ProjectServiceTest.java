package com.trekker.domain.project.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.entity.Job;
import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dao.ProjectRepository;
import com.trekker.domain.project.dto.req.ProjectReqDto;
import com.trekker.domain.project.dto.res.ProjectWithMemberInfoResDto;
import com.trekker.domain.project.dto.res.ProjectWithTaskCompletedList;
import com.trekker.domain.project.entity.Project;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import com.trekker.domain.retrospective.dto.res.ProjectSkillSummaryResDto;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.dto.SkillCountDto;
import com.trekker.domain.task.dto.TaskRetrospectiveSkillDto;
import com.trekker.domain.task.dto.res.TaskRetrospectiveResDto;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private RetrospectiveSkillRepository retrospectiveSkillRepository;
    private Member mockMember;
    private static final Long memberId =1L;

    @BeforeEach
    void setUp() {
        mockMember = Member.builder()
                .id(memberId)
                .build();
    }

    @DisplayName("새로운 프로젝트를 추가한다.")
    @Test
    void addProject() {
        // given
        ProjectReqDto mockDto = mock(ProjectReqDto.class);
        Project saveProject = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .startDate(LocalDate.now())
                .build();

        when(projectRepository.save(any())).thenReturn(saveProject);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        //when
        Long projectId = projectService.addProject(memberId, mockDto);

        //then
        assertThat(projectId).isEqualTo(saveProject.getId());
    }

    @DisplayName("종료 날짜가 시작 날짜보다 이전인 경우 프로젝트 추가에 실패한다.")
    @Test
    void addProjectFailDueToInvalidDates() {
        // given
        ProjectReqDto invalidReqDto = new ProjectReqDto(
                "Test Project",
                "Test Description",
                LocalDate.now(),
                LocalDate.now().minusDays(1),
                "개인"
        );

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // when & then
        assertThatThrownBy(() -> projectService.addProject(memberId, invalidReqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.PROJECT_BAD_REQUEST.getMessage());
    }

    @DisplayName("회원의 프로젝트 리스트를 조회한다.")
    @Test
    void getProjectList() {
        // given
        Project project = Project.builder()
                .id(1L)
                .title("Test Project")
                .startDate(LocalDate.now())
                .type("개인")
                .build();
        List<Project> projects = List.of(project);

        Member mockMember = Member.builder()
                .name("테스트")
                .job(Job.toJob("백엔드"))
                .projectList(projects)
                .build();

        when(memberRepository.findByIdWithJob(memberId)).thenReturn(
                Optional.of(mockMember));
        when(projectRepository.findFilteredProjects(memberId, "개인")).thenReturn(projects);

        // when
        ProjectWithMemberInfoResDto resDto = projectService.getProjectList(memberId, "개인");

        // then
        assertThat(resDto.projectList().size()).isEqualTo(1);
        assertThat(resDto.projectList().get(0).title()).isEqualTo(project.getTitle());

    }

    @DisplayName("프로젝트의 상위 3개 소프트 스킬과 하드 스킬을 반환한다.")
    @Test
    void getProjectSkillSummary() {
        // given
        Long projectId = 1L;
        Project mockProject = mock(Project.class);

        List<SkillCountDto> softSkills = Arrays.asList(
                new SkillCountDto("Communication", 10L),
                new SkillCountDto("Teamwork", 8L),
                new SkillCountDto("Leadership", 6L)
        );

        List<SkillCountDto> hardSkills = Arrays.asList(
                new SkillCountDto("Java", 12L),
                new SkillCountDto("Spring", 9L),
                new SkillCountDto("Hibernate", 7L)
        );

        doNothing().when(mockProject).validateOwner(memberId);
        when(projectRepository.findProjectByIdWIthMember(projectId)).thenReturn(Optional.of(mockProject));
        when(retrospectiveSkillRepository.findTopSkillsByType(projectId, "soft", PageRequest.of(0, 3)))
                .thenReturn(softSkills);
        when(retrospectiveSkillRepository.findTopSkillsByType(projectId, "hard", PageRequest.of(0, 3)))
                .thenReturn(hardSkills);


        // when
        // 테스트 대상 메서드 호출
        ProjectSkillSummaryResDto result = projectService.getProjectSkillSummary(memberId, projectId);

        // then
        assertThat(result.topSoftSkillList()).isEqualTo(softSkills);
        assertThat(result.topHardSkillList()).isEqualTo(hardSkills);
    }

    @DisplayName("기존의 프로젝트를 수정한다.")
    @Test
    void updateProject() {
        // given
        ProjectReqDto reqDto = new ProjectReqDto(
                "update Project",
                "update Description",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "개인"
        );
        Project project = Project.builder()
                .id(1L)
                .member(mockMember)
                .title("Test Project")
                .startDate(LocalDate.now())
                .build();

        when(projectRepository.findProjectByIdWIthMember(project.getId())).thenReturn(
                Optional.of(project));

        //when
        projectService.updateProject(memberId, project.getId(), reqDto);

        //then
        assertThat(project.getTitle()).isEqualTo(reqDto.title());
    }

    @DisplayName("기존의 프로젝트를 삭제한다.")
    @Test
    void deleteProject() {
        // given
        Project project = Project.builder()
                .id(1L)
                .member(mockMember)
                .build();

        when(projectRepository.findProjectByIdWIthMember(project.getId())).thenReturn(
                Optional.of(project));

        // when
        projectService.deleteProject(memberId, project.getId());

        // then
        verify(projectRepository, times(1)).delete(project);
    }


    @DisplayName("프로젝트가 존재하지 않아 삭제에 실패한다.")
    @Test
    void deleteProjectFailDueToProjectNotFound() {
        // given
        Long projectId = 1L;

        when(projectRepository.findProjectByIdWIthMember(projectId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectService.deleteProject(memberId, projectId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.PROJECT_NOT_FOUND.getMessage());
    }

    @DisplayName("프로젝트의 소유자가 달라 삭제에 실패한다.")
    @Test
    void deleteProjectFailUserIsNotOwner() {
        // given
        Member otherMember = Member.builder()
                .id(2L)
                .email("otherUser@example.com")
                .build();
        Project project = Project.builder()
                .id(1L)
                .member(otherMember)
                .title("Test Project")
                .startDate(LocalDate.now())
                .build();

        when(projectRepository.findProjectByIdWIthMember(project.getId())).thenReturn(
                Optional.of(project));

        // when & then
        assertThatThrownBy(() -> projectService.deleteProject(memberId, project.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED_EXCEPTION.getMessage());
    }

    @DisplayName("회원의 프로젝트별 회고 개수를 조회한다.")
    @Test
    void getTotalRetrospectivesProject() {
        // given
        Long projectId1 = 1L;
        Long projectId2 = 2L;
        String projectName1 = "Project 1";
        String projectName2 = "Project 2";
        Long retrospectiveCount1 = 5L;
        Long retrospectiveCount2 = 3L;

        ProjectWithTaskCompletedList dto1 = new ProjectWithTaskCompletedList(projectId1, projectName1, retrospectiveCount1);
        ProjectWithTaskCompletedList dto2 = new ProjectWithTaskCompletedList(projectId2, projectName2, retrospectiveCount2);

        List<ProjectWithTaskCompletedList> mockResult = Arrays.asList(dto1, dto2);

        when(projectRepository.findProjectWithTaskCompleted(memberId)).thenReturn(mockResult);

        // when
        List<ProjectWithTaskCompletedList> result = projectService.getTotalRetrospectivesProject(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
    }

    @DisplayName("회원이 참여한 프로젝트가 없을 때 빈 리스트를 반환한다.")
    @Test
    void getTotalRetrospectivesProject_NoProjects() {
        // given
        when(projectRepository.findProjectWithTaskCompleted(memberId)).thenReturn(List.of());

        // when
        List<ProjectWithTaskCompletedList> result = projectService.getTotalRetrospectivesProject(memberId);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("특정 프로젝트의 회고 리스트를 조회한다.")
    @Test
    void getProjectRetrospectiveList() {
        // given
        Long projectId = 1L;
        // Mock TaskRetrospectiveSkillDto 리스트
        TaskRetrospectiveSkillDto dto1 = new TaskRetrospectiveSkillDto(1L, LocalDate.now(),
                LocalDate.now().plusDays(7), null, "soft", "Communication");
        TaskRetrospectiveSkillDto dto2 = new TaskRetrospectiveSkillDto(1L, LocalDate.now(),
                LocalDate.now().plusDays(7), null, "hard", "Java");
        TaskRetrospectiveSkillDto dto3 = new TaskRetrospectiveSkillDto(2L,
                LocalDate.now(), LocalDate.now().plusDays(5), null, "soft",
                "Problem Solving");

        List<TaskRetrospectiveSkillDto> mockTaskRetrospectives = Arrays.asList(dto1, dto2, dto3);

        when(taskRepository.findTaskRetrospectivesByProjectIdAndMemberId(projectId, memberId))
                .thenReturn(mockTaskRetrospectives);

        // when
        List<TaskRetrospectiveResDto> result = projectService.getProjectRetrospectiveList(memberId,
                projectId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).taskId()).isEqualTo(dto1.taskId());
        assertThat(result.get(0).softSkillList().get(0)).isEqualTo(dto1.skillName());
        assertThat(result.get(1).softSkillList().get(0)).isEqualTo(dto3.skillName());
    }
}