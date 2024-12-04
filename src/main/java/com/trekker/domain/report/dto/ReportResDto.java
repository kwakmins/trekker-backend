package com.trekker.domain.report.dto;

import com.trekker.domain.task.dto.SkillCountDto;
import java.util.List;

public record ReportResDto(
        List<SkillCountDto> softSkillList,
        List<SkillCountDto> hardSkillList
) {

}
