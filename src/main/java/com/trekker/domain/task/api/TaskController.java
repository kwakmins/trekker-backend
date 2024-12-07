package com.trekker.domain.task.api;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.project.dto.res.ProjectWithTaskInfoResDto;
import com.trekker.domain.task.application.TaskService;
import com.trekker.domain.task.dto.req.TaskReqDto;
import com.trekker.global.config.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project/{projectId}/tasks")
@Tag(name = "Task", description = "작업(Task) 관리 API")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(
            summary = "작업 생성",
            description = "특정 프로젝트에 새로운 작업(Task)을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "작업 생성 성공")
    })
    public ResponseEntity<Long> addTask(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1") @PathVariable(name = "projectId") Long projectId,
            @Valid @RequestBody TaskReqDto taskReqDto
    ) {
        Long taskId = taskService.addTask(memberId, projectId, taskReqDto);
        return ResponseEntity.status(CREATED).body(taskId);
    }

    @GetMapping
    @Operation(
            summary = "작업 목록 조회",
            description = "특정 프로젝트에 속한 작업(Task) 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업 목록 조회 성공")
    })
    public ResponseEntity<ProjectWithTaskInfoResDto> getTaskList(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1") @PathVariable(name = "projectId") Long projectId,
            @Parameter(description = "조회 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate reqDate
    ) {
        ProjectWithTaskInfoResDto resDto = taskService.getTaskList(memberId, projectId, reqDate);
        return ResponseEntity.ok(resDto);
    }

    @PutMapping("/{taskId}")
    @Operation(
            summary = "작업 수정",
            description = "특정 작업(Task)의 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "작업 수정 성공")
    })
    public ResponseEntity<Void> updateTask(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "작업 ID", example = "10") @PathVariable(name = "taskId") Long taskId,
            @Valid @RequestBody TaskReqDto taskReqDto
    ) {
        taskService.updateTask(memberId, taskId, taskReqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{taskId}")
    @Operation(
            summary = "작업 삭제",
            description = "특정 작업(Task)을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "작업 삭제 성공")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "작업 ID", example = "10") @PathVariable(name = "taskId") Long taskId
    ) {
        taskService.deleteTask(memberId, taskId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}