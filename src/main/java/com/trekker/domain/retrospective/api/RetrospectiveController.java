package com.trekker.domain.retrospective.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.retrospective.application.RetrospectiveService;
import com.trekker.domain.retrospective.dto.req.RetrospectiveReqDto;
import com.trekker.domain.retrospective.dto.res.RetrospectiveResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/{taskId}/retrospectives")
@Tag(name = "Retrospective", description = "작업 회고 관련 API")
public class RetrospectiveController {

    private final RetrospectiveService retrospectiveService;

    @PostMapping
    @Operation(
            summary = "회고 추가",
            description = "특정 작업에 대한 회고를 추가합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "회고 추가 성공")
    public ResponseEntity<Long> addRetrospective(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "작업 ID", example = "1")
            @PathVariable(name = "taskId") Long taskId,
            @Valid @RequestBody RetrospectiveReqDto reqDto
    ) {
        Long retrospectiveId = retrospectiveService.addRetrospective(memberId, taskId, reqDto);
        return ResponseEntity.status(CREATED).body(retrospectiveId);
    }

    @GetMapping
    @Operation(
            summary = "회고 조회",
            description = "특정 작업의 회고를 조회합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "회고 조회 성공")
    public ResponseEntity<RetrospectiveResDto> getRetrospective(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "작업 ID", example = "1")
            @PathVariable(name = "taskId") Long taskId
    ) {
        RetrospectiveResDto retrospective = retrospectiveService.getRetrospective(memberId, taskId);
        return ResponseEntity.ok(retrospective);
    }

    @PutMapping
    @Operation(
            summary = "회고 수정",
            description = "특정 작업의 회고를 수정합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "회고 수정 성공")
    public ResponseEntity<Void> updateRetrospective(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "작업 ID", example = "1")
            @PathVariable(name = "taskId") Long taskId,
            @Valid @RequestBody RetrospectiveReqDto reqDto
    ) {
        retrospectiveService.updateRetrospective(memberId, taskId, reqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping
    @Operation(
            summary = "회고 삭제",
            description = "특정 작업의 회고를 삭제합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "회고 삭제 성공")
    public ResponseEntity<Void> deleteRetrospective(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "작업 ID", example = "1")
            @PathVariable(name = "taskId") Long taskId
    ) {
        retrospectiveService.deleteRetrospective(memberId, taskId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}