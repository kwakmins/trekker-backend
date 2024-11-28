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
import com.trekker.domain.project.entity.Project;
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
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MemberRepository memberRepository;
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

        when(memberRepository.findByIdWithProjectList(memberId, "개인")).thenReturn(
                Optional.of(mockMember));

        // when
        ProjectWithMemberInfoResDto resDto = projectService.getProjectList(memberId, "개인");

        // then
        assertThat(resDto.projectList().size()).isEqualTo(1);
        assertThat(resDto.projectList().get(0).title()).isEqualTo(project.getTitle());

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
}