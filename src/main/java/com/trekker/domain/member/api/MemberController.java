package com.trekker.domain.member.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.member.application.MemberService;
import com.trekker.domain.member.dto.req.MemberUpdateReqDto;
import com.trekker.domain.member.dto.req.OnboardingReqDto;
import com.trekker.domain.member.dto.res.MemberPortfolioResDto;
import com.trekker.domain.member.dto.res.MemberResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public ResponseEntity<MemberResDto> getMember(@LoginMember Long memberId) {
        MemberResDto member = memberService.getMember(memberId);

        return ResponseEntity.ok(member);
    }

    @PostMapping("/onboarding")
    public ResponseEntity<Void> onBoarding(@LoginMember Long memberId,
            @Valid @RequestBody OnboardingReqDto onboardingReqDto) {
        memberService.updateOnboarding(memberId, onboardingReqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateMember(@LoginMember Long memberId,
            @Valid @RequestBody MemberUpdateReqDto reqDto) {
        memberService.updateMember(memberId, reqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping("/profile/portfolio")
    public ResponseEntity<MemberPortfolioResDto> getPortfolio(@LoginMember Long memberId) {
        MemberPortfolioResDto portfolio = memberService.getPortfolio(memberId);
        return ResponseEntity.ok(portfolio);
    }

}