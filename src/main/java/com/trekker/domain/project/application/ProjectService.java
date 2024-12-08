package com.trekker.domain.project.application;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dao.ProjectRepository;
import com.trekker.domain.project.dao.ProjectRetrospectiveRepository;
import com.trekker.domain.project.dto.req.ProjectExtendReqDto;
import com.trekker.domain.project.dto.req.ProjectReqDto;
import com.trekker.domain.project.dto.req.ProjectRetrospectiveReqDto;
import com.trekker.domain.project.dto.res.ProjectResDto;
import com.trekker.domain.project.dto.res.ProjectWithMemberInfoResDto;
import com.trekker.domain.project.dto.res.ProjectWithTaskCompletedList;
import com.trekker.domain.project.entity.Project;
import com.trekker.domain.project.util.ProjectProgressCalculator;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import com.trekker.domain.retrospective.dto.res.ProjectSkillSummaryResDto;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.dto.SkillCountDto;
import com.trekker.domain.task.dto.TaskRetrospectiveSkillDto;
import com.trekker.domain.task.dto.res.TaskRetrospectiveResDto;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private static final String SOFT_SKILL = "소프트";
    private static final String HARD_SKILL = "하드";

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final RetrospectiveSkillRepository retrospectiveSkillRepository;
    private final ProjectRetrospectiveRepository projectRetrospectiveRepository;

    @Transactional
    public Long addProject(Long memberId, ProjectReqDto projectReqDto) {
        // 회원 조회 및 검증
        Member member = findById(memberId);

        // 종료 날짜가 시작 날짜보다 이전인지 검증
        validateProjectDates(projectReqDto.startDate(), projectReqDto.endDate());

        // dto를 Entity로 변경
        Project project = projectReqDto.toEntity(member);

        // project 저장
        Project saveProject = projectRepository.save(project);
        return saveProject.getId();
    }

    /**
     * 사용자의 프로젝트 리스트를 반환하는 메서드.
     * 회원 정보를 조회하고, 회원이 소유한 프로젝트 리스트를 가져와 진행률과 함께 반환합니다.
     *
     * @param memberId 사용자의 id
     * @param type  프로젝트 유형 ("개인" 또는 "팀"으로 필터링)
     * @return 사용자 정보(이름,직군), 프로젝트 정보 및 진행률을 포함한 DTO 리스트
     * @throws BusinessException 회원 정보가 존재하지 않을 경우 예외를 발생
     */

    public ProjectWithMemberInfoResDto getProjectList(Long memberId, String type) {
        // 회원과 프로젝트 리스트 조회 및 검증
        Member member = memberRepository.findByIdWithJob(memberId).orElseThrow(
                () -> new BusinessException(memberId, "memberId", ErrorCode.MEMBER_NOT_FOUND)
        );

        // 프로젝트 리스트 조회 및 진행률 계산 후 DTO 변환
        List<ProjectResDto> projectList = projectRepository.findFilteredProjects(memberId, type)
                .stream()
                .map(project -> {
                    // 진행률 계산
                    int progress = ProjectProgressCalculator.calculateProjectProgress(
                            project.getStartDate(),
                            project.getEndDate(),
                            LocalDate.now()
                    );
                    // DTO로 변환
                    return ProjectResDto.toDto(project, progress);
                })
                .collect(Collectors.toList());

        return ProjectWithMemberInfoResDto.toDto(member, projectList);
    }

    /**
     * 회원의 작성한 회고의 개수를 프로젝트 별로 조회한다.
     *
     * @param memberId 회원의 Id
     * @return 프로젝트 ID, 이름, 회고의 개수가 담긴 DTO
     */
    public List<ProjectWithTaskCompletedList> getTotalRetrospectivesProject(Long memberId) {
        // 회고의 개수를 할일의 완료 여부로 조회
        return projectRepository.findProjectWithTaskCompleted(memberId);
    }

    /**
     * 특정 프로젝트의 회고 리스트를 조회하는 메소드
     *
     * @param memberId  조회할 회원의 ID
     * @param projectId 조회할 프로젝트의 ID
     * @return TaskRetrospectiveResDto 리스트
     */
    public List<TaskRetrospectiveResDto> getProjectRetrospectiveList(Long memberId, Long projectId) {
        List<TaskRetrospectiveSkillDto> taskSkillDto = taskRepository.findTaskRetrospectivesByProjectIdAndMemberId(projectId, memberId);
        // taskId를 기준으로 그룹화
        Map<Long, List<TaskRetrospectiveSkillDto>> groupedByTaskId = taskSkillDto.stream()
                .collect(Collectors.groupingBy(TaskRetrospectiveSkillDto::taskId));

        // taskId를 기준으로 정렬한 후, 그룹화된 데이터를 TaskRetrospectiveResDto로 매핑
        return groupedByTaskId.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // taskId 오름차순 정렬
                .map(entry -> mapToTaskRetrospectiveResDto(entry.getValue()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateProject(Long memberId, Long projectId, ProjectReqDto projectReqDto) {
        // 회원 및 프로젝트 조회 및 검증
        Project project = findProjectByIdWithMember(projectId);

        // 종료 날짜가 시작 날짜보다 이전인지 검증
        validateProjectDates(projectReqDto.startDate(), projectReqDto.endDate());

        // 회원이 프로젝트 소유자인지 검증
        project.validateOwner(memberId);

        // 프로젝트 업데이트
        project.updateProject(projectReqDto);
    }

    @Transactional
    public void deleteProject(Long memberId, Long projectId) {
        // 회원 및 프로젝트 조회 및 검증
        Project project = findProjectByIdWithMember(projectId);

        // 회원이 프로젝트 소유자인지 검증
        project.validateOwner(memberId);

        // 프로젝트 삭제
        projectRepository.delete(project);
    }

    public ProjectSkillSummaryResDto getProjectSkillSummary(Long memberId, Long projectId) {
        // 회원 및 프로젝트 조회 및 검증
        Project project = findProjectByIdWithMember(projectId);
        project.validateOwner(memberId);

        // 상위 3개의 소프트 스킬 반환
        List<SkillCountDto> topSoftSkills = retrospectiveSkillRepository.findTopSkillsByType(
                projectId, SOFT_SKILL, PageRequest.of(0, 3));

        // 상위 3개의 하드 스킬 반환
        List<SkillCountDto> topHardSkills = retrospectiveSkillRepository.findTopSkillsByType(
                projectId, HARD_SKILL, PageRequest.of(0, 3));

        return ProjectSkillSummaryResDto.toDto(project, topSoftSkills, topHardSkills);
    }

    @Transactional
    public void closeProject(Long memberId, Long projectId, ProjectRetrospectiveReqDto reqDto) {
        // 회원 및 프로젝트 조회 및 검증
        Project project = findProjectByIdWithMember(projectId);
        project.validateOwner(memberId);

        project.updateCompleted();

        projectRetrospectiveRepository.save(reqDto.toEntity(project));
    }

    @Transactional
    public void extendProject(Long memberId, Long projectId, ProjectExtendReqDto reqDto) {
        // 회원 및 프로젝트 조회 및 검증
        Project project = findProjectByIdWithMember(projectId);
        project.validateOwner(memberId);

        // 종료 날짜가 시작 날짜보다 이전인지 검증
        validateProjectDates(project.getStartDate(), reqDto.endDate());

        // 종료 날짜 업데이트
        project.updateEndDate(reqDto.endDate());
    }


    /**
     * 리스트에서 Task의 기본 정보와 스킬 데이터를 추출하여 반환합니다.
     */
    private TaskRetrospectiveResDto mapToTaskRetrospectiveResDto(List<TaskRetrospectiveSkillDto> taskProjections) {
        TaskRetrospectiveSkillDto taskDto = taskProjections.get(0);

        return TaskRetrospectiveResDto.toDto(
                taskDto,
                extractSkills(taskProjections, SOFT_SKILL),
                extractSkills(taskProjections, HARD_SKILL)
        );
    }

    /**
     * 특정 스킬 타입에 해당하는 스킬 이름을 추출합니다.
     *
     * @param taskProjections TaskRetrospectiveSkillDto 리스트
     * @param skillType        추출할 스킬 타입 (예: "소프트", "하드")
     * @return 스킬 이름 리스트
     */
    private List<String> extractSkills(List<TaskRetrospectiveSkillDto> taskProjections, String skillType) {
        return taskProjections.stream()
                .filter(p -> skillType.equalsIgnoreCase(p.skillType()))
                .map(TaskRetrospectiveSkillDto::skillName)
                .filter(name -> !name.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 회원을 조회하고 없으면 예외를 발생시킵니다.
     *
     * @param memberId 조회할 회원의 Id
     * @return Member
     */
    private Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(
                        () -> new BusinessException(memberId, "memberId", ErrorCode.MEMBER_NOT_FOUND)
                );
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
     * 종료날자가 시작 날짜 이전인지 검증
     */
    private static void validateProjectDates(LocalDate startDate, LocalDate endDate) {
        if (endDate != null) {
            if (endDate.isBefore(startDate)) {
                throw new BusinessException(endDate, "endDate",
                        ErrorCode.PROJECT_BAD_REQUEST);
            }
        }
    }


}
