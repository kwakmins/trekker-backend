package com.trekker.domain.task.api;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.project.dto.res.ProjectWithTaskInfoResDto;
import com.trekker.domain.task.api.docs.TaskApi;
import com.trekker.domain.task.application.TaskService;
import com.trekker.domain.task.dto.req.TaskReqDto;
import com.trekker.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project/{projectId}/tasks")
public class TaskController implements TaskApi {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Long> addTask(
            @LoginMember Long memberId,
            @PathVariable(name = "projectId") Long projectId,
            @Valid @RequestBody TaskReqDto taskReqDto
    ) {
        Long taskId = taskService.addTask(memberId, projectId, taskReqDto);
        return ResponseEntity.status(CREATED).body(taskId);
    }

    @GetMapping
    public ResponseEntity<ProjectWithTaskInfoResDto> getTaskList(
            @LoginMember Long memberId,
            @PathVariable(name = "projectId") Long projectId,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate reqDate
    ) {
        ProjectWithTaskInfoResDto resDto = taskService.getTaskList(memberId, projectId, reqDate);
        return ResponseEntity.ok(resDto);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Void> updateTask(
            @LoginMember Long memberId,
            @PathVariable(name = "taskId") Long taskId,
            @Valid @RequestBody TaskReqDto taskReqDto
    ) {
        taskService.updateTask(memberId, taskId, taskReqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @LoginMember Long memberId,
            @PathVariable(name = "taskId") Long taskId
    ) {
        taskService.deleteTask(memberId, taskId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}