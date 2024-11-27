package com.trekker.domain.project.application;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dao.ProjectRepository;
import com.trekker.domain.project.dto.req.ProjectReqDto;
import com.trekker.domain.project.dto.res.ProjectResDto;
import com.trekker.domain.project.dto.res.ProjectWithMemberInfoResDto;
import com.trekker.domain.project.entity.Project;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long addProject(String email, ProjectReqDto projectReqDto) {
        // 회원 조회 및 검증
        Member member = findMemberByEmail(email);

        // 종료 날짜가 시작 날짜보다 이전인지 검증
        validateProjectDates(projectReqDto);

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
     * @param email 사용자의 이메일
     * @param type  프로젝트 유형 ("개인" 또는 "팀"으로 필터링)
     * @return 사용자 정보(이름,직군), 프로젝트 정보 및 진행률을 포함한 DTO 리스트
     * @throws BusinessException 회원 정보가 존재하지 않을 경우 예외를 발생
     */

    public ProjectWithMemberInfoResDto getProjectList(String email, String type) {
        // 회원과 프로젝트 리스트 조회 및 검증
        Member member = memberRepository.findMemberByEmailWithProjectList(email, type).orElseThrow(
                () -> new BusinessException(email, "email", ErrorCode.MEMBER_NOT_FOUND)
        );
        // 프로젝트 진행률 계산 후 DTO로 변환
        List<ProjectResDto> projectList = member.getProjectList().stream()
                .map(ProjectResDto::toDto)
                .collect(Collectors.toList());

        return ProjectWithMemberInfoResDto.toDto(member, projectList);
    }

    @Transactional
    public void updateProject(String email, Long projectId, ProjectReqDto projectReqDto) {
        // 회원 및 프로젝트 조회 및 검증
        Member member = findMemberByEmail(email);
        Project project = findProjectByIdWithMember(projectId);

        // 종료 날짜가 시작 날짜보다 이전인지 검증
        validateProjectDates(projectReqDto);

        // 회원이 프로젝트 소유자인지 검증
        project.validateOwner(member);

        // 프로젝트 업데이트
        project.updateProject(projectReqDto);
    }

    @Transactional
    public void deleteProject(String email, Long projectId) {
        // 회원 및 프로젝트 조회 및 검증
        Member member = findMemberByEmail(email);
        Project project = findProjectByIdWithMember(projectId);

        // 회원이 프로젝트 소유자인지 검증
        project.validateOwner(member);

        // 프로젝트 삭제
        projectRepository.delete(project);
    }

    /**
     * 이메일로 회원을 조회하고 없으면 예외를 발생시킵니다.
     *
     * @param email 조회할 회원의 이메일
     * @return Member
     */
    private Member findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email)
                .orElseThrow(
                        () -> new BusinessException(email, "email", ErrorCode.MEMBER_NOT_FOUND)
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
    private static void validateProjectDates(ProjectReqDto projectReqDto) {
        if (projectReqDto.endDate() != null) {
            if (projectReqDto.endDate().isBefore(projectReqDto.startDate())) {
                throw new BusinessException(projectReqDto.endDate(), "endDate",
                        ErrorCode.PROJECT_BAD_REQUEST);
            }
        }
    }


}
