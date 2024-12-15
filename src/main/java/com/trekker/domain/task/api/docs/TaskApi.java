package com.trekker.domain.task.api.docs;

import com.trekker.domain.project.dto.res.ProjectWithTaskInfoResDto;
import com.trekker.domain.task.dto.req.TaskReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import java.time.LocalDate;

@Tag(name = "Task", description = "작업(Task) 관리 API")
public interface TaskApi {

    @Operation(
            summary = "작업 생성",
            description = "특정 프로젝트에 새로운 작업(Task)을 생성합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "201", description = "작업 생성 성공" )
    ResponseEntity<Long> addTask(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1" ) Long projectId,
            @Valid TaskReqDto taskReqDto
    );

    @Operation(
            summary = "작업 목록 조회",
            description = "특정 프로젝트에 속한 작업(Task) 목록을 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "200", description = "작업 목록 조회 성공" )
    ResponseEntity<ProjectWithTaskInfoResDto> getTaskList(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "프로젝트 ID", example = "1" ) Long projectId,
            @Parameter(description = "조회 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01" )
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reqDate
    );

    @Operation(
            summary = "작업 수정",
            description = "특정 작업(Task)의 정보를 수정합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "작업 수정 성공" )
    ResponseEntity<Void> updateTask(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "작업 ID", example = "10" ) Long taskId,
            @Valid TaskReqDto taskReqDto
    );

    @Operation(
            summary = "작업 삭제",
            description = "특정 작업(Task)을 삭제합니다.",
            security = @SecurityRequirement(name = "BearerAuth" )
    )
    @ApiResponse(responseCode = "204", description = "작업 삭제 성공" )
    ResponseEntity<Void> deleteTask(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "작업 ID", example = "10" ) Long taskId
    );
}