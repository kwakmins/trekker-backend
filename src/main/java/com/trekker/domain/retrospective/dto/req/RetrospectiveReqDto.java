package com.trekker.domain.retrospective.dto.req;

import com.trekker.domain.retrospective.entity.Retrospective;
import com.trekker.domain.task.entity.Task;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record RetrospectiveReqDto(

        @NotNull(message = "소프트 스킬 목록은 필수입니다.")
        List<String> softSkillList,

        @NotNull(message = "하드 스킬 목록은 필수입니다.")
        List<String> hardSkillList,

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