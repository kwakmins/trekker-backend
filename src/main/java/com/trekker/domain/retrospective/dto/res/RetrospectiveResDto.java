package com.trekker.domain.retrospective.dto.res;

import com.trekker.domain.retrospective.entity.Retrospective;
import java.util.List;
import lombok.Builder;

@Builder
public record RetrospectiveResDto(

        String taskName,

        // 소프트 스틸 목록
        List<String> softSkillList,

        // 하드 스킬 목록
        List<String> hardSkillList,

        // 회고  내용
        String content
) {

    public static RetrospectiveResDto toDto(String taskName, Retrospective retrospective) {
        // 소프트 및 하드 스킬 목록 초기화
        List<String> softSkillList = retrospective.getRetrospectiveSkillList().stream()
                .filter(skill -> "소프트".equals(skill.getType()))
                .map(skill -> skill.getSkill().getName())
                .toList();

        List<String> hardSkillList = retrospective.getRetrospectiveSkillList().stream()
                .filter(skill -> "하드".equals(skill.getType()))
                .map(skill -> skill.getSkill().getName())
                .toList();

        // DTO 빌더를 통해 반환
        return RetrospectiveResDto.builder()
                .taskName(taskName)
                .softSkillList(softSkillList)
                .hardSkillList(hardSkillList)
                .content(retrospective.getContent()) // 회고 내용 가져오기
                .build();
    }
}
