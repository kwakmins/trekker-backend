package com.trekker.domain.retrospective.api;

import com.trekker.domain.retrospective.application.SkillService;
import com.trekker.domain.retrospective.dto.res.SkillDetailResDto;
import com.trekker.domain.retrospective.dto.res.SkillSummaryResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skill")
@Tag(name = "Skill", description = "스킬 관련 API")
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/retrospective")
    @Operation(
            summary = "회원 스킬 요약 조회",
            description = "회원의 회고 데이터 기반으로 스킬 요약 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스킬 요약 조회 성공"),
    })
    public ResponseEntity<List<SkillSummaryResDto>> getSkillSummary(
            @Parameter(hidden = true) @LoginMember Long memberId) {
        List<SkillSummaryResDto> skillSummaries = skillService.getSkillSummaryByMemberId(memberId);
        return ResponseEntity.ok(skillSummaries);
    }

    @GetMapping("/retrospective/{skillId}")
    @Operation(
            summary = "특정 스킬 상세 조회",
            description = "회원의 특정 스킬에 대한 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스킬 상세 조회 성공"),
    })
    public ResponseEntity<List<SkillDetailResDto>> getSkillDetails(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "스킬 ID", example = "1") @PathVariable Long skillId) {
        List<SkillDetailResDto> skillDetails = skillService.getSkillDetailsBySkillIdAndMemberId(
                memberId, skillId);
        return ResponseEntity.ok(skillDetails);
    }

}