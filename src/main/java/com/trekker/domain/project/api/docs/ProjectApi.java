package com.trekker.domain.project.api.docs;

import com.trekker.domain.project.dto.req.ProjectExtendReqDto;
import com.trekker.domain.project.dto.req.ProjectReqDto;
import com.trekker.domain.project.dto.req.ProjectRetrospectiveReqDto;
import com.trekker.domain.project.dto.res.ProjectWithMemberInfoResDto;
import com.trekker.domain.project.dto.res.ProjectWithTaskCompletedList;
import com.trekker.domain.retrospective.dto.res.ProjectSkillSummaryResDto;
import com.trekker.domain.task.dto.res.TaskRetrospectiveResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Project", description = "프로젝트 관련 API")
public interface ProjectApi {

    @Operation(
            summary = "프로젝트 생성",
            description = "새로운 프로젝트를 생성합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "201", description = "프로젝트 생성 성공" )
    ResponseEntity<Long> addProject(@Parameter(hidden = true) Long memberId,
            @Valid ProjectReqDto projectReqDto);

    @Operation(
            summary = "프로젝트 목록 조회",
            description = "회원의 프로젝트 목록을 조회합니다. 프로젝트 타입을 필터링할 수 있습니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공" )
    ResponseEntity<ProjectWithMemberInfoResDto> getProjectList(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "프로젝트 타입 (예: 개인, 팀)", example = "개인" ) String type
    );

    @Operation(
            summary = "전체 회고 조회",
            description = "회원의 전체 프로젝트 회고를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "전체 회고 조회 성공" )
    ResponseEntity<List<ProjectWithTaskCompletedList>> getTotalRetrospectivesProject(
            @Parameter(hidden = true) Long memberId);

    @Operation(
            summary = "프로젝트 회고 조회",
            description = "특정 프로젝트의 회고를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "프로젝트 회고 조회 성공" )
    ResponseEntity<List<TaskRetrospectiveResDto>> getRetrospectivesProject(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1" ) Long projectId);

    @Operation(
            summary = "프로젝트 수정",
            description = "특정 프로젝트의 정보를 수정합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "프로젝트 수정 성공" )
    ResponseEntity<Void> updateProject(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1" ) Long projectId,
            @Valid ProjectReqDto projectReqDto
    );

    @Operation(
            summary = "프로젝트 삭제",
            description = "특정 프로젝트를 삭제합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "프로젝트 삭제 성공" )
    ResponseEntity<Void> deleteProject(@Parameter(hidden = true) Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1" ) Long projectId);

    @Operation(
            summary = "프로젝트 기술 요약 조회",
            description = "특정 프로젝트의 기술 요약 정보를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "프로젝트 기술 요약 조회 성공" )
    ResponseEntity<ProjectSkillSummaryResDto> getProjectSkillSummary(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1" ) Long projectId);

    @Operation(
            summary = "프로젝트 종료",
            description = "특정 프로젝트를 종료합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "프로젝트 종료 성공" )
    ResponseEntity<Void> closeProject(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1" ) Long projectId,
            @Valid ProjectRetrospectiveReqDto reqDto
    );

    @Operation(
            summary = "프로젝트 연장",
            description = "특정 프로젝트의 기간을 연장합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "프로젝트 연장 성공" )
    ResponseEntity<Void> extendProject(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1" ) Long projectId,
            @Valid ProjectExtendReqDto reqDto
    );
}