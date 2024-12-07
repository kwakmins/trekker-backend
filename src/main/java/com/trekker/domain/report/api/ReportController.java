package com.trekker.domain.report.api;

import com.trekker.domain.report.dto.ReportResDto;
import com.trekker.domain.report.service.ReportService;
import com.trekker.domain.task.dto.SkillCountDto;
import com.trekker.global.config.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@Tag(name = "Report", description = "회원 통계 및 리포트 관련 API")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @Operation(
            summary = "회원 통계 리포트 조회",
            description = "회원의 통계 리포트를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "회원 리포트 조회 성공")
    public ResponseEntity<ReportResDto> getMemberReport(
            @Parameter(hidden = true) @LoginMember Long memberId
    ) {
        ReportResDto memberReport = reportService.getMemberReport(memberId);
        return ResponseEntity.ok(memberReport);
    }

    @GetMapping("/skill")
    @Operation(
            summary = "회원 스킬 통계 조회",
            description = """
                    회원의 스킬별 통계를 조회합니다.
                    - `type` 파라미터를 사용해 통계 종류를 선택할 수 있습니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "회원 스킬 통계 조회 성공")
    public ResponseEntity<List<SkillCountDto>> getMemberSkill(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "스킬 통계 타입 (예: 소프트, 하드)", example = "소프트")
            @RequestParam String type
    ) {
        List<SkillCountDto> memberSkillList = reportService.getMemberSkillList(memberId, type);
        return ResponseEntity.ok(memberSkillList);
    }
}