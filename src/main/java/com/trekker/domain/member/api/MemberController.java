package com.trekker.domain.member.api;

import com.trekker.domain.member.api.docs.MemberApi;
import com.trekker.domain.member.application.MemberService;
import com.trekker.domain.member.dto.req.MemberFeedbackReqDto;
import com.trekker.domain.member.dto.req.MemberUpdateReqDto;
import com.trekker.domain.member.dto.req.OnboardingReqDto;
import com.trekker.domain.member.dto.res.MemberPortfolioResDto;
import com.trekker.domain.member.dto.res.MemberResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController implements MemberApi {

    private final MemberService memberService;

    @GetMapping("/profile")
    public ResponseEntity<MemberResDto> getMember(@LoginMember Long memberId) {
        MemberResDto member = memberService.getMember(memberId);
        return ResponseEntity.ok(member);
    }

    @PostMapping("/onboarding")
    public ResponseEntity<Void> onBoarding(
            @LoginMember Long memberId,
            @Valid @RequestBody OnboardingReqDto onboardingReqDto
    ) {
        memberService.updateOnboarding(memberId, onboardingReqDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateMember(
            @LoginMember Long memberId,
            @RequestPart("data") @Valid MemberUpdateReqDto reqDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        memberService.updateMember(memberId, reqDto, profileImage);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile/portfolio")
    public ResponseEntity<MemberPortfolioResDto> getPortfolio(@LoginMember Long memberId) {
        MemberPortfolioResDto portfolio = memberService.getPortfolio(memberId);
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping("/feedback")
    public ResponseEntity<Void> saveFeedback(
            @LoginMember Long memberId,
            @RequestBody MemberFeedbackReqDto feedbackReqDto
    ) {
        memberService.saveFeedBack(memberId, feedbackReqDto);
        return ResponseEntity.noContent().build();
    }
}