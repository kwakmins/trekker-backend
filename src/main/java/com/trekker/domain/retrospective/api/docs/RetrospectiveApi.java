package com.trekker.domain.retrospective.api.docs;

import com.trekker.domain.retrospective.dto.req.RetrospectiveReqDto;
import com.trekker.domain.retrospective.dto.res.RetrospectiveResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

@Tag(name = "Retrospective", description = "작업 회고 관련 API")
public interface RetrospectiveApi {

    @Operation(
            summary = "회고 추가",
            description = "특정 작업에 대한 회고를 추가합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "201", description = "회고 추가 성공" )
    ResponseEntity<Long> addRetrospective(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "작업 ID", example = "1" ) Long taskId,
            @Valid RetrospectiveReqDto reqDto
    );

    @Operation(
            summary = "회고 조회",
            description = "특정 작업의 회고를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "회고 조회 성공" )
    ResponseEntity<RetrospectiveResDto> getRetrospective(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "작업 ID", example = "1" ) Long taskId
    );

    @Operation(
            summary = "회고 수정",
            description = "특정 작업의 회고를 수정합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "회고 수정 성공" )
    ResponseEntity<Void> updateRetrospective(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "작업 ID", example = "1" ) Long taskId,
            @Valid RetrospectiveReqDto reqDto
    );

    @Operation(
            summary = "회고 삭제",
            description = "특정 작업의 회고를 삭제합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "회고 삭제 성공" )
    ResponseEntity<Void> deleteRetrospective(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "작업 ID", example = "1" ) Long taskId
    );
}