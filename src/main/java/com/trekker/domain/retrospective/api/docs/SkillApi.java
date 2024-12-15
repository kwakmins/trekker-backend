package com.trekker.domain.retrospective.api.docs;

import com.trekker.domain.retrospective.dto.res.SkillDetailResDto;
import com.trekker.domain.retrospective.dto.res.SkillSummaryResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Skill", description = "스킬 관련 API")
public interface SkillApi {

    @Operation(
            summary = "회원 스킬 요약 조회",
            description = "회원의 회고 데이터 기반으로 스킬 요약 정보를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "스킬 요약 조회 성공" )
    ResponseEntity<List<SkillSummaryResDto>> getSkillSummary(
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "특정 스킬 상세 조회",
            description = "회원의 특정 스킬에 대한 상세 정보를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "스킬 상세 조회 성공" )
    ResponseEntity<List<SkillDetailResDto>> getSkillDetails(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "스킬 ID", example = "1" ) Long skillId
    );
}