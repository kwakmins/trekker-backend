package com.trekker.domain.retrospective.dto.res;

import com.trekker.domain.retrospective.entity.Retrospective;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record RetrospectiveResDto(

        @Schema(description = "작업 이름", example = "백엔드 API 개발")
        String taskName,

        @Schema(description = "소프트 스킬 목록 (예: 커뮤니케이션, 팀워크 등)", example = "[\"커뮤니케이션\", \"팀워크\"]")
        List<String> softSkillList,

        @Schema(description = "하드 스킬 목록 (예: Spring Boot, JPA 등)", example = "[\"Spring Boot\", \"JPA\"]")
        List<String> hardSkillList,

        @Schema(description = "회고 내용", example = "이번 작업을 통해 문제 해결 능력이 향상되었습니다.")
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