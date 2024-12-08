package com.trekker.domain.retrospective.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.trekker.domain.retrospective.dao.SkillRepository;
import com.trekker.domain.retrospective.dto.res.SkillDetailResDto;
import com.trekker.domain.retrospective.dto.res.SkillSummaryResDto;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    SkillService skillService;
    @Mock
    SkillRepository skillRepository;

    @DisplayName("스킬 별 사용 횟수를 조회합니다.")
    @Test
    void getSkillSummaryByMemberId() {
        // given
        Long memberId = 1L;
        List<SkillSummaryResDto> mockSummaries = Arrays.asList(
                new SkillSummaryResDto(1L, "Java", 5L),
                new SkillSummaryResDto(2L, "Spring", 3L)
        );
        when(skillRepository.findAllSkillsWithCountByMemberId(memberId)).thenReturn(mockSummaries);

        // when
        List<SkillSummaryResDto> summaries = skillService.getSkillSummaryByMemberId(memberId);

        // then
        assertThat(summaries).hasSize(2);
        verify(skillRepository, times(1)).findAllSkillsWithCountByMemberId(memberId);
    }

    @DisplayName("특정 스킬에 대한 상세 정보를 조회합니다.")
    @Test
    void getSkillDetailsBySkillIdAndMemberId() {
        // given
        Long memberId = 1L;
        Long skillId = 1L;
        List<SkillDetailResDto> mockDetails = Arrays.asList(
                new SkillDetailResDto(1L, LocalDate.now(), null,
                        "Task A", "Retrospective A"),
                new SkillDetailResDto(2L, LocalDate.now(), null,
                        "Task B", "Retrospective B")
        );
        when(skillRepository.findSkillDetailsBySkillIdAndMemberId(skillId, memberId)).thenReturn(
                mockDetails);

        // when
        List<SkillDetailResDto> details = skillService.getSkillDetailsBySkillIdAndMemberId(memberId,
                skillId);

        // then
        assertThat(details).hasSize(2);
        verify(skillRepository, times(1)).findSkillDetailsBySkillIdAndMemberId(skillId, memberId);
    }
}