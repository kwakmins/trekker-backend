package com.trekker.domain.retrospective.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.entity.Project;
import com.trekker.domain.retrospective.dao.RetrospectiveRepository;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import com.trekker.domain.retrospective.dao.SkillRepository;
import com.trekker.domain.retrospective.dto.req.RetrospectiveReqDto;
import com.trekker.domain.retrospective.dto.res.RetrospectiveResDto;
import com.trekker.domain.retrospective.entity.Retrospective;
import com.trekker.domain.retrospective.entity.RetrospectiveSkill;
import com.trekker.domain.retrospective.entity.Skill;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.entity.Task;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetrospectiveServiceTest {

    @InjectMocks
    private RetrospectiveService retrospectiveService;
    @Mock
    private RetrospectiveRepository retrospectiveRepository;
    @Mock
    private RetrospectiveSkillRepository retrospectiveSkillRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private TaskRepository taskRepository;

    private Task task;
    private Skill softSkill;
    private Skill hardSkill;
    private RetrospectiveReqDto reqDto;
    private Retrospective retrospective;

    private Long memberId;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .isCompleted(false)
                .project(Project.builder().id(1L).member(Member.builder().id(1L).build()).build())
                .build();

        softSkill = Skill.builder()
                .id(1L)
                .name("커뮤니케이션")
                .build();

        hardSkill = Skill.builder()
                .id(2L)
                .name("Spring")
                .build();

        reqDto = RetrospectiveReqDto.builder()
                .content("Updated Retrospective")
                .softSkillList(List.of("Java"))
                .hardSkillList(List.of("Spring"))
                .build();

        retrospective = Retrospective.builder()
                .id(1L)
                .content("Initial Retrospective")
                .task(task)
                .retrospectiveSkillList(new ArrayList<>())
                .build();

        memberId = 1L;
    }

    @DisplayName("새로운 회고를 추가합니다.")
    @Test
    void addRetrospective() {
        // given
        when(taskRepository.findTaskByIdWithProjectAndMemberWithRetrospective(task.getId()))
                .thenReturn(Optional.of(task));
        when(retrospectiveRepository.save(any(Retrospective.class))).thenReturn(retrospective);
        when(skillRepository.findByNameIn(anySet()))
                .thenReturn(Arrays.asList(softSkill, hardSkill));

        // when
        Long retrospectiveId = retrospectiveService.addRetrospective(memberId, task.getId(), reqDto);

        // then
        assertThat(retrospectiveId).isEqualTo(retrospective.getId());
        assertThat(task.getIsCompleted()).isEqualTo(true);
        verify(retrospectiveRepository, times(1)).save(any(Retrospective.class));
    }

    @DisplayName("존재하지 않는 태스크로 회고를 추가하려 하면 예외가 발생한다.")
    @Test
    void addRetrospectiveFailTaskNotFound() {
        // given
        when(taskRepository.findTaskByIdWithProjectAndMemberWithRetrospective(task.getId()))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> retrospectiveService.addRetrospective(memberId, task.getId(), reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.TASK_NOT_FOUND.getMessage());
    }

    @DisplayName("이미 완료된 테스크에 회고를 추가하려 하면 예외가 발생한다.")
    @Test
    void addRetrospectiveFailTaskAlreadyCompleted() {
        // given
        Task completedTask = Task.builder()
                .id(1L)
                .project(Project.builder().id(1L).member(Member.builder().id(memberId).build()).build())
                .isCompleted(true)
                .build();

        when(taskRepository.findTaskByIdWithProjectAndMemberWithRetrospective(task.getId()))
                .thenReturn(Optional.of(completedTask));

        // when
        assertThatThrownBy(() -> retrospectiveService.addRetrospective(memberId, task.getId(), reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.TASK_BAD_REQUEST.getMessage());

    }

    @DisplayName("기존 회고를 조회한다.")
    @Test
    void getRetrospective() {
        // given
        retrospective.getRetrospectiveSkillList().add(
                RetrospectiveSkill.builder()
                        .id(1L)
                        .skill(softSkill)
                        .type("소프트")
                        .retrospective(retrospective)
                        .build()
        );

        when(taskRepository.findTaskByIdWithProjectAndMemberWithRetrospective(task.getId()))
                .thenReturn(Optional.of(task));
        when(retrospectiveRepository.findByIdWithSkillList(retrospective.getId()))
                .thenReturn(Optional.of(retrospective));

        // when
        RetrospectiveResDto resDto = retrospectiveService.getRetrospective(memberId, task.getId(),
                retrospective.getId());

        // then
        assertThat(resDto.softSkillList().size()).isEqualTo(1);
        assertThat(resDto.content()).isEqualTo(retrospective.getContent());
    }

    @DisplayName("기존 회고를 업데이트 한다.")
    @Test
    void updateRetrospective() {
        // given
        RetrospectiveReqDto updateDto = RetrospectiveReqDto.builder()
                .content("Updated Content")
                .softSkillList(List.of("Java"))
                .hardSkillList(List.of("Spring"))
                .build();

        retrospective.getRetrospectiveSkillList().add(
                RetrospectiveSkill.builder()
                        .id(1L)
                        .skill(softSkill)
                        .type("소프트")
                        .retrospective(retrospective)
                        .build()
        );

        when(taskRepository.findTaskByIdWithProjectAndMemberWithRetrospective(task.getId()))
                .thenReturn(Optional.of(task));
        when(retrospectiveRepository.findByIdWithSkillList(retrospective.getId()))
                .thenReturn(Optional.of(retrospective));
        when(skillRepository.findByNameIn(anySet()))
                .thenReturn(Arrays.asList(softSkill, hardSkill));

        // when
        retrospectiveService.updateRetrospective(memberId, task.getId(), retrospective.getId(),
                updateDto);

        // then
        assertThat(retrospective.getContent()).isEqualTo(updateDto.content());
        verify(retrospectiveSkillRepository, times(1)).deleteAll(anyList());
        verify(retrospectiveSkillRepository, times(1)).saveAll(anyList());
    }


    @DisplayName("기존 회고를 삭제한다.")
    @Test
    void deleteRetrospective() {
        // given
        Task completedTask = Task.builder()
                .id(1L)
                .project(Project.builder().id(1L).member(Member.builder().id(memberId).build()).build())
                .isCompleted(true)
                .build();

        when(taskRepository.findTaskByIdWithProjectAndMemberWithRetrospective(task.getId()))
                .thenReturn(Optional.of(completedTask));
        when(retrospectiveRepository.findByIdWithSkillList(retrospective.getId()))
                .thenReturn(Optional.of(retrospective));

        // when
        retrospectiveService.deleteRetrospective(memberId, task.getId(), retrospective.getId());

        // then
        assertThat(task.getIsCompleted()).isEqualTo(false);
        verify(retrospectiveRepository, times(1)).delete(retrospective);
    }
}