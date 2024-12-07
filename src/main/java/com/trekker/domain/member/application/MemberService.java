package com.trekker.domain.member.application;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.dto.req.MemberUpdateReqDto;
import com.trekker.domain.member.dto.req.OnboardingReqDto;
import com.trekker.domain.member.dto.res.MemberPortfolioResDto;
import com.trekker.domain.member.dto.res.MemberResDto;
import com.trekker.domain.member.entity.Member;
import com.trekker.domain.project.dto.ProjectSkillDto;
import com.trekker.domain.project.dto.res.ProjectSkillResDto;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import com.trekker.global.service.file.FileService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private static final String SOFT_SKILL = "소프트";
    private static final String HARD_SKILL = "하드";

    private final FileService fileService;
    private final MemberRepository memberRepository;
    private final RetrospectiveSkillRepository retrospectiveSkillRepository;

    /**
     * 회원의 정보를 조회합니다
     *
     * @param memberId 회원의 Id
     * @return 회원의 이름, 직무, 프로필 경로 가 담긴 DTO
     */
    public MemberResDto getMember(Long memberId) {
        Member member = findByIdWithJob(memberId);

        return MemberResDto.toDto(member);
    }

    /**
     * 온보딩 데이터를 업데이트 합니다.
     *
     * @param memberId         사용자의 id
     * @param onboardingReqDto 사용자의 이름, 직무명, 프로젝트 정보가 포함되어 있음
     */
    @Transactional
    public void updateOnboarding(Long memberId, OnboardingReqDto onboardingReqDto) {
        Member member = findByIdWithJob(memberId);
        Boolean isCompleted = member.getOnboarding().getIsCompleted();
        if (isCompleted) {
            throw new BusinessException(isCompleted, "isCompleted",
                    ErrorCode.MEMBER_ONBOARDING_ALREADY_COMPLETED);
        }
        member.updateOnboarding(onboardingReqDto);
    }

    /**
     * 회원 정보를 업데이트 합니다. 요청에 프로필 이미지가 포함된 경우, 기존 프로필 이미지를 삭제하고 새 이미지를 저장합니다.
     *
     * @param memberId 사용자의 ID
     * @param reqDto   회원 업데이트 정보 DTO
     */
    @Transactional
    public void updateMember(Long memberId, MemberUpdateReqDto reqDto, MultipartFile profileImage) {
        Member member = findByIdWithJob(memberId);

        // 프로필 이미지가 요청에 포함된 경우
        if (profileImage != null) {
            handleProfileImageUpdate(member, profileImage);
        }

        // 나머지 멤버 정보 업데이트
        member.updateMember(reqDto);
    }

    /**
     * 회원의 포트폴리오 데이터를 반환합니다.
     *
     * @param memberId 조회할 회원의 ID
     * @return 회원 정보와 프로젝트별 상위 스킬 데이터를 포함한 포트폴리오 DTO
     */
    public MemberPortfolioResDto getPortfolio(Long memberId) {
        // 회원 정보 조회 (직무 포함)
        Member member = findByIdWithJob(memberId);

        // 회원의 프로젝트 스킬 데이터 조회
        List<ProjectSkillDto> skillDto = retrospectiveSkillRepository.findProjectSkillsByMemberId(
                memberId);

        // 프로젝트별 상위 스킬 데이터를 가공
        List<ProjectSkillResDto> projectSkillResDto = groupAndTransformProjectSkills(skillDto);

        // 회원 정보와 프로젝트 데이터를 기반으로 포트폴리오 생성
        return MemberPortfolioResDto.toDto(member, projectSkillResDto);
    }


    /**
     * 프로젝트별로 데이터를 그룹화하고, 각 프로젝트별로 상위 7개의 소프트/하드 스킬을 추출합니다.
     *
     * @param skillDto 조회된 프로젝트 스킬 데이터
     * @return 각 프로젝트별로 상위 7개의 스킬을 포함한 DTO 목록
     */
    private List<ProjectSkillResDto> groupAndTransformProjectSkills(
            List<ProjectSkillDto> skillDto) {
        return skillDto.stream()
                // 프로젝트 ID를 기준으로 그룹화
                .collect(Collectors.groupingBy(
                        ProjectSkillDto::projectId, // 그룹화 기준: 프로젝트 ID
                        LinkedHashMap::new,         // 순서 보장을 위한 LinkedHashMap 사용
                        Collectors.toList()         // 각 그룹에 대한 데이터 목록 수집
                ))
                .values()
                .stream()
                // 각 그룹(프로젝트)에 대해 DTO로 변환
                .map(this::transformToProjectSkillResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 프로젝트에 대해 상위 7개의 소프트/하드 스킬을 추출하고, 프로젝트 정보를 포함한 DTO를 생성합니다.
     *
     * @param projectSkills 해당 프로젝트의 스킬 데이터
     * @return 프로젝트의 상위 7개의 스킬 정보를 포함한 DTO
     */
    private ProjectSkillResDto transformToProjectSkillResponse(
            List<ProjectSkillDto> projectSkills) {

        // 상위 7개의 소프트 스킬 추출
        List<String> softSkillList = extractTopSkills(projectSkills, SOFT_SKILL)
                .stream()
                .map(ProjectSkillDto::skillName)
                .collect(Collectors.toList());

        // 상위 7개의 하드 스킬 추출
        List<String> hardSkillList = extractTopSkills(projectSkills, HARD_SKILL)
                .stream()
                .map(ProjectSkillDto::skillName)
                .collect(Collectors.toList());

        // 첫 번째 항목에서 프로젝트 기본 정보를 추출
        ProjectSkillDto project = projectSkills.get(0);

        // 응답 DTO 생성
        return ProjectSkillResDto.toDto(project, softSkillList, hardSkillList);
    }

    /**
     * 특정 스킬 타입(소프트/하드)에 대해 상위 7개의 스킬을 추출합니다.
     *
     * @param projectSkills 특정 프로젝트의 스킬 데이터
     * @param skillType     스킬 타입 ("soft" 또는 "hard")
     * @return 상위 7개의 스킬 리스트
     */
    private List<ProjectSkillDto> extractTopSkills(List<ProjectSkillDto> projectSkills,
            String skillType) {
        return projectSkills.stream()
                // 스킬 타입 필터링 (soft 또는 hard)
                .filter(dto -> skillType.equalsIgnoreCase(dto.skillType()))
                // 상위 7개의 데이터만 추출
                .limit(7)
                .collect(Collectors.toList());
    }

    /**
     * 기존 이미지를 삭제한 후, 새로운 이미지를 저장하고, 저장된 이미지의 경로를 회원 엔티티에 업데이트합니다.
     *
     * @param member          회원 엔티티
     * @param newProfileImage 새로 업로드된 프로필 이미지 파일
     */
    private void handleProfileImageUpdate(Member member, MultipartFile newProfileImage) {
        String existingImage = member.getProfileImage();

        // 기존 이미지 삭제
        if (existingImage != null && !existingImage.isEmpty()) {
            String fileName = extractFileName(existingImage);
            fileService.deleteProfileImage(fileName);
        }

        // 새 이미지 저장
        String newImagePath = fileService.saveProfileImage(newProfileImage);

        // 멤버에 새 이미지 경로 업데이트
        member.updateProfileImage(newImagePath);
    }

    /**
     * 이미지 경로에서 파일명을 추출합니다.
     *
     * @param imagePath 이미지 경로 (예: "/uploads/profile-images/image.png")
     * @return 추출된 파일명 (예: "image.png")
     */
    private String extractFileName(String imagePath) {
        return imagePath.substring(imagePath.lastIndexOf("/") + 1);
    }

    /**
     * 회원 ID 로 회원을 조회하고 없으면 예외를 발생시킵니다.
     *
     * @param memberId 회원 ID
     * @return Member
     */
    private Member findByIdWithJob(Long memberId) {
        return memberRepository.findByIdWithJob(memberId)
                .orElseThrow(
                        () -> new BusinessException(memberId, "memberId",
                                ErrorCode.MEMBER_NOT_FOUND));
    }

}
