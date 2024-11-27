package com.trekker.domain.project.application;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dao.ProjectRepository;
import com.trekker.domain.project.dto.ProjectReqDto;
import com.trekker.domain.project.entity.Project;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
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

        // dto를 Entity로 변경
        Project project = projectReqDto.toEntity(member);

        // project 저장
        Project saveProject = projectRepository.save(project);
        return saveProject.getId();
    }

    @Transactional
    public void updateProject(String email, Long projectId, ProjectReqDto projectReqDto) {
        // 회원 및 프로젝트 조회 및 검증
        Member member = findMemberByEmail(email);
        Project project = findProjectByIdWithMember(projectId);

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


}
