package com.trekker.domain.project.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.project.application.ProjectService;
import com.trekker.domain.project.dto.req.ProjectExtendReqDto;
import com.trekker.domain.project.dto.req.ProjectReqDto;
import com.trekker.domain.project.dto.req.ProjectRetrospectiveReqDto;
import com.trekker.domain.project.dto.res.ProjectWithMemberInfoResDto;
import com.trekker.domain.project.dto.res.ProjectWithTaskCompletedList;
import com.trekker.domain.retrospective.dto.res.ProjectSkillSummaryResDto;
import com.trekker.domain.task.dto.res.TaskRetrospectiveResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
@Tag(name = "Project", description = "프로젝트 관련 API")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(
            summary = "프로젝트 생성",
            description = "새로운 프로젝트를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "프로젝트 생성 성공")
            }
    )
    public ResponseEntity<Long> addProject(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Valid @RequestBody ProjectReqDto projectReqDto
    ) {
        Long projectId = projectService.addProject(memberId, projectReqDto);
        return ResponseEntity.status(CREATED).body(projectId);
    }

    @GetMapping
    @Operation(
            summary = "프로젝트 목록 조회",
            description = "회원의 프로젝트 목록을 조회합니다. 프로젝트 타입을 필터링할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공")
            }
    )
    public ResponseEntity<ProjectWithMemberInfoResDto> getProjectList(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "프로젝트 타입 (예: 개인, 팀)", example = "개인")
            @RequestParam(required = false) String type
    ) {
        ProjectWithMemberInfoResDto resDto = projectService.getProjectList(memberId, type);
        return ResponseEntity.ok(resDto);
    }

    @GetMapping("/retrospective")
    @Operation(
            summary = "전체 회고 조회",
            description = "회원의 전체 프로젝트 회고를 조회합니다. (사용자 회고 조회시 사용)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "전체 회고 조회 성공")
            }
    )
    public ResponseEntity<List<ProjectWithTaskCompletedList>> getTotalRetrospectivesProject(
            @Parameter(hidden = true) @LoginMember Long memberId
    ) {
        List<ProjectWithTaskCompletedList> totalRetrospectives =
                projectService.getTotalRetrospectivesProject(memberId);
        return ResponseEntity.ok(totalRetrospectives);
    }

    @GetMapping("/retrospective/{projectId}")
    @Operation(
            summary = "프로젝트 회고 조회",
            description = "특정 프로젝트의 회고를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로젝트 회고 조회 성공")
            }
    )
    public ResponseEntity<List<TaskRetrospectiveResDto>> getRetrospectivesProject(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId
    ) {
        List<TaskRetrospectiveResDto> projectRetrospectiveList = projectService.getProjectRetrospectiveList(
                memberId, projectId);
        return ResponseEntity.ok(projectRetrospectiveList);
    }

    @PutMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 수정",
            description = "특정 프로젝트의 정보를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "프로젝트 수정 성공")
            }
    )
    public ResponseEntity<Void> updateProject(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable(name = "projectId") Long projectId,
            @Valid @RequestBody ProjectReqDto projectReqDto
    ) {
        projectService.updateProject(memberId, projectId, projectReqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 삭제",
            description = "특정 프로젝트를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "프로젝트 삭제 성공")
            }
    )
    public ResponseEntity<Void> deleteProject(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable(name = "projectId") Long projectId
    ) {
        projectService.deleteProject(memberId, projectId);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping("/{projectId}/skill-summary")
    @Operation(
            summary = "프로젝트 기술 요약 조회(프로젝트 종료시 조회)",
            description = "특정 프로젝트의 기술 요약 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로젝트 기술 요약 조회 성공")
            }
    )
    public ResponseEntity<ProjectSkillSummaryResDto> getProjectSkillSummary(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable(name = "projectId") Long projectId
    ) {
        ProjectSkillSummaryResDto summary = projectService.getProjectSkillSummary(memberId,
                projectId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/{projectId}/close")
    @Operation(
            summary = "프로젝트 종료",
            description = "특정 프로젝트를 종료합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "프로젝트 종료 성공")
            }
    )
    public ResponseEntity<Void> closeProject(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable(name = "projectId") Long projectId,
            @Valid @RequestBody ProjectRetrospectiveReqDto reqDto
    ) {
        projectService.closeProject(memberId, projectId, reqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{projectId}/extend")
    @Operation(
            summary = "프로젝트 연장",
            description = "특정 프로젝트의 기간을 연장합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "프로젝트 연장 성공")
            }
    )
    public ResponseEntity<Void> extendProject(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable(name = "projectId") Long projectId,
            @Valid @RequestBody ProjectExtendReqDto reqDto
    ) {
        projectService.extendProject(memberId, projectId, reqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}