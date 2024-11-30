package com.trekker.domain.retrospective.application;

import com.trekker.domain.retrospective.dao.RetrospectiveRepository;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import com.trekker.domain.retrospective.dao.SkillRepository;
import com.trekker.domain.retrospective.dto.req.RetrospectiveReqDto;
import com.trekker.domain.retrospective.dto.res.RetrospectiveResDto;
import com.trekker.domain.retrospective.entity.Retrospective;
import com.trekker.domain.retrospective.entity.RetrospectiveSkill;
import com.trekker.domain.retrospective.entity.Skill;
import com.trekker.domain.task.dao.TaskRepository;
import com.trekker.domain.task.entity.Task;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrospectiveService {

    private static final String SOFT_SKILL = "소프트";
    private static final String HARD_SKILL = "하드";


    private final RetrospectiveRepository retrospectiveRepository;
    private final RetrospectiveSkillRepository retrospectiveSkillRepository;
    private final SkillRepository skillRepository;
    private final TaskRepository taskRepository;

    /**
     * 새로운 회고를 추가
     */
    @Transactional
    public Long addRetrospective(Long memberId, Long taskId, RetrospectiveReqDto reqDto) {
        // 1. 할 일 작성자 확인
        Task task = validateTaskOwnership(memberId, taskId);
        validateTaskCompletion(task);

        // 2. 새로운 회고 엔티티 생성 및 저장
        Retrospective retrospective = retrospectiveRepository.save(reqDto.toEntity(task));

        // 3. 스킬 데이터 조회 또는 생성
        Map<String, Skill> skillMap = findOrCreateSkills(reqDto);

        // 4. 회고와 연결된 스킬 저장
        saveRetrospectiveSkills(retrospective, reqDto, skillMap);

        // 5. 태스크 완료 상태 업데이트
        task.updateCompleted(true);

        return retrospective.getId();
    }

    public RetrospectiveResDto getRetrospective(Long memberId, Long taskId, Long retrospectiveId) {
        // 1. 할 일 작성자 확인
        Task task = validateTaskOwnership(memberId, taskId);

        // 2. 회고 및 연관 스킬 조회
        Retrospective retrospective = findRetrospectiveByIdWithSkillList(retrospectiveId);

        // 3. DTO로 변환 및 반환
        return RetrospectiveResDto.toDto(task.getName(), retrospective);
    }

    /**
     * 기존 회고를 업데이트
     */
    @Transactional
    public void updateRetrospective(Long memberId, Long taskId, Long retrospectiveId,
            RetrospectiveReqDto reqDto) {
        // 1. 할 일 작성자 확인
        validateTaskOwnership(memberId, taskId);

        // 2. 회고 엔티티 및 관련 데이터 조회
        Retrospective retrospective = findRetrospectiveByIdWithSkillList(retrospectiveId);

        // 3. 회고 내용 업데이트
        retrospective.updateContent(reqDto.content());

        // 4. 기존 스킬과 새로운 스킬 비교 및 갱신
        updateRetrospectiveSkills(retrospective, reqDto);
    }

    @Transactional
    public void deleteRetrospective(Long memberId, Long taskId, Long retrospectiveId) {
        // 1. 할 일 작성자 확인
        Task task = validateTaskOwnership(memberId, taskId);

        // 2. 회고 엔티티 및 관련 데이터 조회
        Retrospective retrospective = findRetrospectiveByIdWithSkillList(retrospectiveId);

        // 3. 태스크 완료 상태 업데이트
       task.updateCompleted(false);

       retrospectiveRepository.delete(retrospective);
    }

    /**
     * 회고와 연결된 스킬 데이터를 저장
     * <p>
     * 요청된 소프트 및 하드 스킬 데이터를 회고 엔티티와 연결하여 RetrospectiveSkill 엔티티로 변환하고, 이를 데이터베이스에 Batch 방식으로 저장합니다.
     * 스킬 이름을 기준으로 Skill 객체를 매핑하여 처리합니다.
     *
     * @param retrospective 회고 엔티티
     * @param reqDto        요청 DTO
     * @param skillMap      스킬 이름과 객체 매핑
     */
    private void saveRetrospectiveSkills(Retrospective retrospective, RetrospectiveReqDto reqDto,
            Map<String, Skill> skillMap) {
        // 1. 요청된 스킬 데이터를 소프트/하드로 구분하여 처리
        List<RetrospectiveSkill> retrospectiveSkills = Stream.concat(
                reqDto.softSkillList().stream()
                        .map(name -> RetrospectiveSkill.toEntity(SOFT_SKILL, retrospective,
                                skillMap.get(name))),
                reqDto.hardSkillList().stream()
                        .map(name -> RetrospectiveSkill.toEntity(HARD_SKILL, retrospective,
                                skillMap.get(name)))
        ).toList();

        // 2. Batch 저장 처리
        retrospectiveSkillRepository.saveAll(retrospectiveSkills);
    }

    /**
     * 기존 회고의 스킬 데이터를 업데이트
     *
     * @param retrospective 회고 엔티티
     * @param reqDto        요청 DTO
     */
    private void updateRetrospectiveSkills(Retrospective retrospective,
            RetrospectiveReqDto reqDto) {
        // 1. 요청된 스킬에 대한 Skill 엔티티를 가져옴
        Map<String, Skill> skillMap = findOrCreateSkills(reqDto);

        // 2. 기존 및 요청된 스킬 데이터를 Map으로 변환
        // 기존 RetrospectiveSkill 데이터를 Map 형태로 변환
        Map<String, RetrospectiveSkill> existingSkills = retrospective.getRetrospectiveSkillList()
                .stream()
                .collect(Collectors.toMap(
                        retrospectiveSkill -> retrospectiveSkill.getSkill().getName(), // 스킬 이름
                        retrospectiveSkill -> retrospectiveSkill // RetrospectiveSkill 객체
                ));
        Map<String, String> requestedSkillMap = createRequestedSkillMap(reqDto);

        // 3. 삭제 작업: 요청에 없는 기존 스킬 삭제
        retrospectiveSkillRepository.deleteAll(
                existingSkills.values().stream()
                        .filter(skill -> !requestedSkillMap.containsKey(skill.getSkill().getName()))
                        .toList()
        );

        // 4. 추가 작업: 요청된 스킬 중 기존에 없는 스킬 추가
        retrospectiveSkillRepository.saveAll(
                requestedSkillMap.entrySet().stream()
                        .filter(entry -> !existingSkills.containsKey(entry.getKey()))
                        .map(entry -> RetrospectiveSkill.toEntity(entry.getValue(), retrospective,
                                skillMap.get(entry.getKey())))
                        .toList()
        );
    }

    private Map<String, String> createRequestedSkillMap(RetrospectiveReqDto reqDto) {
        return Stream.concat(
                reqDto.softSkillList().stream().map(skillName -> Map.entry(skillName, SOFT_SKILL)),
                reqDto.hardSkillList().stream().map(skillName -> Map.entry(skillName, HARD_SKILL))
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 요청된 스킬 데이터를 조회하거나 새로 생성하여 반환
     *
     * @param reqDto 요청 DTO
     * @return 스킬 이름과 스킬 객체 매핑
     */
    private Map<String, Skill> findOrCreateSkills(RetrospectiveReqDto reqDto) {
        // 1. 요청된 소프트 스킬과 하드 스킬 이름을 합침
        Set<String> skillNames = new HashSet<>();
        skillNames.addAll(reqDto.softSkillList());
        skillNames.addAll(reqDto.hardSkillList());

        // 2. 기존 스킬 조회
        Map<String, Skill> skillMap = skillRepository.findByNameIn(skillNames).stream()
                .collect(Collectors.toMap(Skill::getName, skill -> skill));

        // 3. 새로운 스킬을 개별적으로 생성
        for (String name : skillNames) {
            if (!skillMap.containsKey(name)) {
                Skill newSkill = Skill.toEntity(name);
                skillRepository.save(newSkill);
                skillMap.put(name, newSkill);   // 맵에 추가
            }
        }

        return skillMap;
    }

    /**
     * 태스크의 소유권을 검증하는 메서드
     *
     * @param memberId 사용자 ID
     * @param taskId   태스크 ID
     * @return 검증된 Task 엔티티
     */
    private Task validateTaskOwnership(Long memberId, Long taskId) {
        Task task = findTaskById(taskId);
        task.getProject().validateOwner(memberId);
        return task;
    }

    /**
     * 특정 ID의 태스크를 조회하는 메서드
     *
     * @param taskId 태스크 ID
     * @return 조회된 Task 엔티티
     */
    private Task findTaskById(Long taskId) {
        return taskRepository.findTaskByIdWithProjectAndMemberWithRetrospective(taskId)
                .orElseThrow(
                        () -> new BusinessException(taskId, "taskId", ErrorCode.TASK_NOT_FOUND));
    }

    /**
     * 태스크가 이미 완료 상태인지 확인하는 메서드
     *
     * @param task 확인할 태스크
     */
    private void validateTaskCompletion(Task task) {
        if (task.getIsCompleted()) {
            throw new BusinessException(task.getId(), "taskId",
                    ErrorCode.RETROSPECTIVE_BAD_REQUEST);
        }
    }

    /**
     * 특정 태스크에 연결된 회고를 조회하는 메서드
     *
     * @param retrospectiveId 회고 ID
     * @return 조회된 Retrospective 엔티티
     */
    private Retrospective findRetrospectiveByIdWithSkillList(Long retrospectiveId) {
        return retrospectiveRepository.findByIdWithSkillList(retrospectiveId)
                .orElseThrow(() -> new BusinessException(retrospectiveId, "retrospectiveId",
                        ErrorCode.RETROSPECTIVE_NOT_FOUND));
    }


}