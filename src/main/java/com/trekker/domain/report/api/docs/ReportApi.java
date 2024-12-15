package com.trekker.domain.report.api.docs;

import com.trekker.domain.report.dto.ReportResDto;
import com.trekker.domain.task.dto.SkillCountDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Report", description = "회원 통계 및 리포트 관련 API")
public interface ReportApi {

    @Operation(
            summary = "회원 통계 리포트 조회",
            description = "회원의 통계 리포트를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "회원 리포트 조회 성공" )
    ResponseEntity<ReportResDto> getMemberReport(
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "회원 스킬 통계 조회",
            description = """
                    회원의 스킬별 통계를 조회합니다.
                    - `type` 파라미터를 사용해 통계 종류를 선택할 수 있습니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "회원 스킬 통계 조회 성공" )
    ResponseEntity<List<SkillCountDto>> getMemberSkill(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "스킬 통계 타입 (예: 소프트, 하드)", example = "소프트" ) String type
    );
}