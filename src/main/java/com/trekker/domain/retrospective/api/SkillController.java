package com.trekker.domain.retrospective.api;

import com.trekker.domain.retrospective.api.docs.SkillApi;
import com.trekker.domain.retrospective.application.SkillService;
import com.trekker.domain.retrospective.dto.res.SkillDetailResDto;
import com.trekker.domain.retrospective.dto.res.SkillSummaryResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skill")
public class SkillController implements SkillApi {

    private final SkillService skillService;

    @GetMapping("/retrospective")
    public ResponseEntity<List<SkillSummaryResDto>> getSkillSummary(
            @LoginMember Long memberId
    ) {
        List<SkillSummaryResDto> skillSummaries = skillService.getSkillSummaryByMemberId(memberId);
        return ResponseEntity.ok(skillSummaries);
    }

    @GetMapping("/retrospective/{skillId}")
    public ResponseEntity<List<SkillDetailResDto>> getSkillDetails(
            @LoginMember Long memberId,
            @PathVariable Long skillId
    ) {
        List<SkillDetailResDto> skillDetails = skillService.getSkillDetailsBySkillIdAndMemberId(
                memberId, skillId);
        return ResponseEntity.ok(skillDetails);
    }
}