package com.trekker.domain.retrospective.api;

import com.trekker.domain.retrospective.application.SkillService;
import com.trekker.domain.retrospective.dto.res.SkillDetailResDto;
import com.trekker.domain.retrospective.dto.res.SkillSummaryResDto;
import com.trekker.global.config.security.annotation.LoginMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skill")
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/retrospective")
    public ResponseEntity<List<SkillSummaryResDto>> getSkillSummary(
            @LoginMember Long memberId) {
        List<SkillSummaryResDto> skillSummaries = skillService.getSkillSummaryByMemberId(memberId);
        return ResponseEntity.ok(skillSummaries);
    }

    @GetMapping("/retrospective/{skillId}")
    public ResponseEntity<List<SkillDetailResDto>> getSkillDetails(
            @LoginMember Long memberId,
            @PathVariable Long skillId) {
        List<SkillDetailResDto> skillDetails = skillService.getSkillDetailsBySkillIdAndMemberId(memberId, skillId);
        return ResponseEntity.ok(skillDetails);
    }

}
