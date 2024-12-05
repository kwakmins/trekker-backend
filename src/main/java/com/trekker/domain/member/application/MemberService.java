package com.trekker.domain.member.application;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.dto.req.MemberUpdateReqDto;
import com.trekker.domain.member.dto.req.OnboardingReqDto;
import com.trekker.domain.member.entity.Member;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileService fileService;

    /**
     * 온보딩 데이터를 업데이트 합니다.
     *
     * @param memberId         사용자의 id
     * @param onboardingReqDto 사용자의 이름, 직무명, 프로젝트 정보가 포함되어 있음
     */
    @Transactional
    public void updateOnboarding(Long memberId, OnboardingReqDto onboardingReqDto) {
        Member member = findByIdWithSocialAndOnboarding(memberId);
        Boolean isCompleted = member.getOnboarding().getIsCompleted();
        if (isCompleted) {
            throw new BusinessException(isCompleted, "isCompleted",
                    ErrorCode.MEMBER_ONBOARDING_ALREADY_COMPLETED);
        }
        member.updateOnboarding(onboardingReqDto);
    }

    /**
     * 회원 정보를 업데이트 합니다.
     * 요청에 프로필 이미지가 포함된 경우, 기존 프로필 이미지를 삭제하고 새 이미지를 저장합니다.
     *
     * @param memberId 사용자의 ID
     * @param reqDto   회원 업데이트 정보 DTO
     */
    @Transactional
    public void updateMember(Long memberId, MemberUpdateReqDto reqDto) {
        Member member = findByIdWithSocialAndOnboarding(memberId);

        // 프로필 이미지가 요청에 포함된 경우
        if (reqDto.profileImage() != null) {
            handleProfileImageUpdate(member, reqDto.profileImage());
        }

        // 나머지 멤버 정보 업데이트
        member.updateMember(reqDto);
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
    private Member findByIdWithSocialAndOnboarding(Long memberId) {
        return memberRepository.findByIdWithSocialAndOnboarding(memberId)
                .orElseThrow(
                        () -> new BusinessException(memberId, "memberId",
                                ErrorCode.MEMBER_NOT_FOUND));
    }

}
