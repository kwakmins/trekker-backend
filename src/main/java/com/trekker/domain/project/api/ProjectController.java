package com.trekker.domain.project.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.project.application.ProjectService;
import com.trekker.domain.project.dto.req.ProjectReqDto;
import com.trekker.domain.project.dto.res.ProjectWithMemberInfoResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Long> addProject(
            @LoginMember Long memberId,
            @Valid @RequestBody ProjectReqDto projectReqDto
    ) {
        Long projectId = projectService.addProject(memberId, projectReqDto);

        return ResponseEntity.status(CREATED).body(projectId);
    }
    @GetMapping
    public ResponseEntity<ProjectWithMemberInfoResDto> getProjectList(
            @LoginMember Long memberId,
            @RequestParam(required = false) String type
    ) {
        ProjectWithMemberInfoResDto resDto = projectService.getProjectList(memberId, type);

        return ResponseEntity.ok(resDto);
    }


    @PutMapping("/{projectId}")
    public ResponseEntity<Void> updateProject(
            @LoginMember Long memberId,
            @PathVariable(name = "projectId") Long projectId,
            @Valid @RequestBody ProjectReqDto projectReqDto
    ) {
        projectService.updateProject(memberId, projectId, projectReqDto);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @LoginMember Long memberId,
            @PathVariable(name = "projectId") Long projectId
            ) {
        projectService.deleteProject(memberId, projectId);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}

