package com.trekker.domain.member.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.member.application.MemberService;
import com.trekker.domain.member.dto.OnboardingReqDto;
import com.trekker.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/onboarding")
    public ResponseEntity<Void> onBoarding(@LoginMember String email,
            @Valid @RequestBody OnboardingReqDto onboardingReqDto) {
        memberService.updateOnboarding(email, onboardingReqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}