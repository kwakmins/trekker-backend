package com.trekker.domain.project.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.project.application.ProjectService;
import com.trekker.domain.project.dto.ProjectReqDto;
import com.trekker.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Long> addProject(
            @LoginMember String email,
            @Valid @RequestBody ProjectReqDto projectReqDto
    ) {
        Long projectId = projectService.addProject(email, projectReqDto);

        return ResponseEntity.status(CREATED).body(projectId);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Void> updateProject(
            @LoginMember String email,
            @PathVariable(name = "projectId") Long projectId,
            @Valid @RequestBody ProjectReqDto projectReqDto
    ) {
        projectService.updateProject(email, projectId, projectReqDto);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @LoginMember String email,
            @PathVariable(name = "projectId") Long projectId
            ) {
        projectService.deleteProject(email, projectId);

        return ResponseEntity.status(NO_CONTENT).build();
    }

}

