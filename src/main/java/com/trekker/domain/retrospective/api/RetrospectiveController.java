package com.trekker.domain.retrospective.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.trekker.domain.retrospective.application.RetrospectiveService;
import com.trekker.domain.retrospective.dto.req.RetrospectiveReqDto;
import com.trekker.domain.retrospective.dto.res.RetrospectiveResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/{taskId}/retrospectives")
public class RetrospectiveController {

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

    @GetMapping("/{retrospectiveId}")
    public ResponseEntity<RetrospectiveResDto> getRetrospective(
            @LoginMember Long memberId,
            @PathVariable(name = "taskId") Long taskId,
            @PathVariable(name = "retrospectiveId") Long retrospectiveId) {
        RetrospectiveResDto retrospective = retrospectiveService.getRetrospective(memberId, taskId,
                retrospectiveId);

        return ResponseEntity.ok(retrospective);
    }

    @PutMapping("/{retrospectiveId}")
    public ResponseEntity<Void> updateRetrospective(
            @LoginMember Long memberId,
            @PathVariable(name = "taskId") Long taskId,
            @PathVariable(name = "retrospectiveId") Long retrospectiveId,
            @Valid @RequestBody RetrospectiveReqDto reqDto
    ) {
        retrospectiveService.updateRetrospective(memberId, taskId, retrospectiveId, reqDto);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{retrospectiveId}")
    public ResponseEntity<Void> deleteRetrospective(
            @LoginMember Long memberId,
            @PathVariable(name = "taskId") Long taskId,
            @PathVariable(name = "retrospectiveId") Long retrospectiveId) {

        retrospectiveService.deleteRetrospective(memberId, taskId,
                retrospectiveId);

        return ResponseEntity.status(NO_CONTENT).build();
    }


}
