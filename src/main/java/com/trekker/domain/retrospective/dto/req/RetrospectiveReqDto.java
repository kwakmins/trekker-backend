package com.trekker.domain.retrospective.dto.req;

import com.trekker.domain.retrospective.entity.Retrospective;
import com.trekker.domain.task.entity.Task;
import java.util.ArrayList;
import java.util.List;

public record RetrospectiveReqDto(

        // 소프트 스틸 목록
        List<String> softSkillList,

        // 하드 스킬 목록
        List<String> hardSkillList,

        // 회고  내용
        String content
) {

    /**
     * Retrospective 엔티티로 변환
     */
    public Retrospective toEntity(Task task) {

        return Retrospective.builder()
                .task(task)
                .content(content)
                .skillList(new ArrayList<>()) // 초기 빈 리스트
                .build();
    }

}
