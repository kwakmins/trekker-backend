package com.trekker.domain.report.service;

import com.trekker.domain.report.dto.ReportResDto;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import com.trekker.domain.task.dto.SkillCountDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final String SOFT_SKILL = "소프트";
    private static final String HARD_SKILL = "소프트";



    private final RetrospectiveSkillRepository retrospectiveSkillRepository;


    public ReportResDto getMemberReport(Long memberId) {

        // 상위 3개의 소프트 스킬 반환
        List<SkillCountDto> topSoftSkills = retrospectiveSkillRepository.findTopSkillsByMemberIdAndType(
                memberId, SOFT_SKILL, PageRequest.of(0, 3));

        // 상위 3개의 하드 스킬 반환
        List<SkillCountDto> topHardSkills = retrospectiveSkillRepository.findTopSkillsByMemberIdAndType(
                memberId, HARD_SKILL, PageRequest.of(0, 3));

        return new ReportResDto(topSoftSkills, topHardSkills);
    }

}
