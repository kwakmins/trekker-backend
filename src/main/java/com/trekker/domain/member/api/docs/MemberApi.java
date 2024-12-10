package com.trekker.domain.member.api.docs;

import com.trekker.domain.member.dto.req.MemberUpdateReqDto;
import com.trekker.domain.member.dto.req.OnboardingReqDto;
import com.trekker.domain.member.dto.res.MemberPortfolioResDto;
import com.trekker.domain.member.dto.res.MemberResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Tag(name = "Member", description = "회원 관련 API")
public interface MemberApi {

    @Operation(
            summary = "회원 프로필 조회",
            description = "회원의 프로필 정보를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 프로필 조회 성공" )
    })
    ResponseEntity<MemberResDto> getMember(
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "온보딩 데이터 업데이트",
            description = "회원의 온보딩 데이터를 업데이트합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "온보딩 데이터 업데이트 성공" )
    })
    ResponseEntity<Void> onBoarding(
            @Parameter(hidden = true) Long memberId,
            @Valid OnboardingReqDto onboardingReqDto
    );

    @Operation(
            summary = "회원 정보 수정",
            description = """
                    회원의 정보를 수정합니다.
                     - 회원 정보 수정 데이터는 **multipart/form-data**의 `data` 파트로 전달해야 합니다.
                     - 프로필 이미지는 선택적으로 `profileImage` 파트로 전달할 수 있습니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 정보 수정 성공" )
    })
    ResponseEntity<Void> updateMember(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "회원 정보 수정 데이터는 **RequestPart의 `data` 파트**로 전달해야 합니다." )
            @Valid MemberUpdateReqDto reqDto,
            @Parameter(description = "선택적 프로필 이미지는 **RequestPart의 `profileImage` 파트**로 전달해야 합니다." )
            MultipartFile profileImage
    );

    @Operation(
            summary = "회원 포트폴리오 조회",
            description = "회원의 포트폴리오 정보를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 포트폴리오 조회 성공" )
    })
    ResponseEntity<MemberPortfolioResDto> getPortfolio(
            @Parameter(hidden = true) Long memberId
    );
}