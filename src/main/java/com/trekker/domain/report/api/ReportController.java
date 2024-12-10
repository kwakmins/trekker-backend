package com.trekker.domain.report.api;

import com.trekker.domain.report.api.docs.ReportApi;
import com.trekker.domain.report.dto.ReportResDto;
import com.trekker.domain.report.service.ReportService;
import com.trekker.domain.task.dto.SkillCountDto;
import com.trekker.global.config.security.annotation.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController implements ReportApi {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ReportResDto> getMemberReport(@LoginMember Long memberId) {
        ReportResDto memberReport = reportService.getMemberReport(memberId);
        return ResponseEntity.ok(memberReport);
    }

    @GetMapping("/skill")
    public ResponseEntity<List<SkillCountDto>> getMemberSkill(
            @LoginMember Long memberId,
            @RequestParam String type
    ) {
        List<SkillCountDto> memberSkillList = reportService.getMemberSkillList(memberId, type);
        return ResponseEntity.ok(memberSkillList);
    }
}