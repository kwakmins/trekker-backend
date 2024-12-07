package com.trekker.domain.retrospective.dto.req;

import com.trekker.domain.retrospective.entity.Retrospective;
import com.trekker.domain.task.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record RetrospectiveReqDto(

        @Schema(description = "소프트 스킬 목록 (예: 커뮤니케이션, 팀워크 등)", example = "[\"커뮤니케이션\", \"팀워크\"]")
        @NotNull(message = "소프트 스킬 목록은 필수입니다.")
        List<String> softSkillList,

        @Schema(description = "하드 스킬 목록 (예: Spring Boot, JPA 등)", example = "[\"Spring Boot\", \"JPA\"]")
        @NotNull(message = "하드 스킬 목록은 필수입니다.")
        List<String> hardSkillList,

        @Schema(description = "회고 내용 (최대 300자)", example = "이번 작업을 통해 문제 해결 능력이 향상되었습니다.")
        @Size(max = 300, message = "내용은 최대 300 자까지 입력 가능합니다.")
        String content
) {

    /**
     * Retrospective 엔티티로 변환
     */
    public Retrospective toEntity(Task task) {

        return Retrospective.builder()
                .task(task)
                .content(content)
                .retrospectiveSkillList(new ArrayList<>()) // 초기 빈 리스트
                .build();
    }

}