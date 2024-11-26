package com.trekker.domain.member.application;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.dto.OnboardingReqDto;
import com.trekker.domain.member.entity.Member;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 온보딩 데이터를 업데이트 합니다.
     *
     * @param email            사용자의 이메일
     * @param onboardingReqDto 사용자의 이름, 직무명, 프로젝트 정보가 포함되어 있음
     */
    @Transactional
    public void updateOnboarding(String email, OnboardingReqDto onboardingReqDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(
                        () -> new BusinessException(email, "email", ErrorCode.MEMBER_NOT_FOUND));
        Boolean isCompleted = member.getOnboarding().getIsCompleted();
        if (isCompleted) {
            throw new BusinessException(isCompleted, "isCompleted",
                    ErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }
        member.updateOnboarding(onboardingReqDto);
    }
}
