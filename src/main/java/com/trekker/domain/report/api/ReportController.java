package com.trekker.domain.report.api;

import com.trekker.domain.report.dto.ReportResDto;
import com.trekker.domain.report.service.ReportService;
import com.trekker.global.config.security.annotation.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ReportResDto> getMemberReport(
            @LoginMember Long memberId
    ) {
        ReportResDto memberReport = reportService.getMemberReport(memberId);

        return ResponseEntity.ok(memberReport);
    }

}
