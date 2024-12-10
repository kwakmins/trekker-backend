package com.trekker.domain.project.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.project.api.docs.ProjectApi;
import com.trekker.domain.project.application.ProjectService;
import com.trekker.domain.project.dto.req.*;
import com.trekker.domain.project.dto.res.*;
import com.trekker.domain.retrospective.dto.res.ProjectSkillSummaryResDto;
import com.trekker.domain.task.dto.res.TaskRetrospectiveResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectController implements ProjectApi {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Long> addProject(@LoginMember Long memberId,
            @Valid @RequestBody ProjectReqDto projectReqDto) {
        Long projectId = projectService.addProject(memberId, projectReqDto);
        return ResponseEntity.status(CREATED).body(projectId);
    }

    @GetMapping
    public ResponseEntity<ProjectWithMemberInfoResDto> getProjectList(@LoginMember Long memberId,
            @RequestParam(required = false) String type) {
        ProjectWithMemberInfoResDto resDto = projectService.getProjectList(memberId, type);
        return ResponseEntity.ok(resDto);
    }

    @GetMapping("/retrospective")
    public ResponseEntity<List<ProjectWithTaskCompletedList>> getTotalRetrospectivesProject(
            @LoginMember Long memberId) {
        List<ProjectWithTaskCompletedList> totalRetrospectives = projectService.getTotalRetrospectivesProject(
                memberId);
        return ResponseEntity.ok(totalRetrospectives);
    }

    @GetMapping("/retrospective/{projectId}")
    public ResponseEntity<List<TaskRetrospectiveResDto>> getRetrospectivesProject(
            @LoginMember Long memberId, @PathVariable Long projectId) {
        List<TaskRetrospectiveResDto> projectRetrospectiveList = projectService.getProjectRetrospectiveList(
                memberId, projectId);
        return ResponseEntity.ok(projectRetrospectiveList);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Void> updateProject(@LoginMember Long memberId,
            @PathVariable Long projectId, @Valid @RequestBody ProjectReqDto projectReqDto) {
        projectService.updateProject(memberId, projectId, projectReqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@LoginMember Long memberId,
            @PathVariable Long projectId) {
        projectService.deleteProject(memberId, projectId);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping("/{projectId}/skill-summary")
    public ResponseEntity<ProjectSkillSummaryResDto> getProjectSkillSummary(
            @LoginMember Long memberId, @PathVariable Long projectId) {
        ProjectSkillSummaryResDto summary = projectService.getProjectSkillSummary(memberId,
                projectId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/{projectId}/close")
    public ResponseEntity<Void> closeProject(@LoginMember Long memberId,
            @PathVariable Long projectId, @Valid @RequestBody ProjectRetrospectiveReqDto reqDto) {
        projectService.closeProject(memberId, projectId, reqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{projectId}/extend")
    public ResponseEntity<Void> extendProject(@LoginMember Long memberId,
            @PathVariable Long projectId, @Valid @RequestBody ProjectExtendReqDto reqDto) {
        projectService.extendProject(memberId, projectId, reqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}