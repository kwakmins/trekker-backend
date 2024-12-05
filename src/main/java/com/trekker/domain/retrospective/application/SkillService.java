package com.trekker.domain.retrospective.application;

import com.trekker.domain.retrospective.dao.SkillRepository;
import com.trekker.domain.retrospective.dto.res.SkillDetailResDto;
import com.trekker.domain.retrospective.dto.res.SkillSummaryResDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;


    /**
     * 특정 회원의 스킬 별 사용 개수를 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 스킬 이름과 사용 개수 리스트
     */
    public List<SkillSummaryResDto> getSkillSummaryByMemberId(Long memberId) {
        return skillRepository.findAllSkillsWithCountByMemberId(memberId);
    }

    /**
     * 특정 회원의 특정 스킬에 대한 상세 정보를 조회합니다.
     *
     * @param memberId 회원 ID
     * @param skillId  스킬 ID
     * @return 할 일 기간, 할 일 이름, 회고 내용 리스트
     */
    public List<SkillDetailResDto> getSkillDetailsBySkillIdAndMemberId(Long memberId, Long skillId) {
        return skillRepository.findSkillDetailsBySkillIdAndMemberId(skillId, memberId);
    }

}
