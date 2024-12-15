package com.trekker.domain.retrospective.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.retrospective.api.docs.RetrospectiveApi;
import com.trekker.domain.retrospective.application.RetrospectiveService;
import com.trekker.domain.retrospective.dto.req.RetrospectiveReqDto;
import com.trekker.domain.retrospective.dto.res.RetrospectiveResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/{taskId}/retrospectives")
public class RetrospectiveController implements RetrospectiveApi {

    private final RetrospectiveService retrospectiveService;

    @PostMapping
    public ResponseEntity<Long> addRetrospective(
            @LoginMember Long memberId,
            @PathVariable(name = "taskId") Long taskId,
            @Valid @RequestBody RetrospectiveReqDto reqDto
    ) {
        Long retrospectiveId = retrospectiveService.addRetrospective(memberId, taskId, reqDto);
        return ResponseEntity.status(CREATED).body(retrospectiveId);
    }

    @GetMapping
    public ResponseEntity<RetrospectiveResDto> getRetrospective(
            @LoginMember Long memberId,
            @PathVariable(name = "taskId") Long taskId
    ) {
        RetrospectiveResDto retrospective = retrospectiveService.getRetrospective(memberId, taskId);
        return ResponseEntity.ok(retrospective);
    }

    @PutMapping
    public ResponseEntity<Void> updateRetrospective(
            @LoginMember Long memberId,
            @PathVariable(name = "taskId") Long taskId,
            @Valid @RequestBody RetrospectiveReqDto reqDto
    ) {
        retrospectiveService.updateRetrospective(memberId, taskId, reqDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRetrospective(
            @LoginMember Long memberId,
            @PathVariable(name = "taskId") Long taskId
    ) {
        retrospectiveService.deleteRetrospective(memberId, taskId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}