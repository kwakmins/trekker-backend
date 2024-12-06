package com.trekker.domain.member.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.trekker.domain.member.dao.MemberRepository;
import com.trekker.domain.member.dto.req.MemberUpdateReqDto;
import com.trekker.domain.member.dto.req.OnboardingReqDto;
import com.trekker.domain.member.dto.res.MemberPortfolioResDto;
import com.trekker.domain.member.dto.res.MemberResDto;
import com.trekker.domain.member.entity.Job;
import com.trekker.domain.member.entity.Member;
import com.trekker.domain.member.entity.Onboarding;
import com.trekker.domain.project.dto.ProjectSkillDto;
import com.trekker.domain.retrospective.dao.RetrospectiveSkillRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;
    @Mock
    FileService fileService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    RetrospectiveSkillRepository retrospectiveSkillRepository;

    private Long memberId;
    private Member mockMember;

    private ProjectSkillDto skillDto1;
    private ProjectSkillDto skillDto2;
    private ProjectSkillDto skillDto3;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        mockMember = Member.builder()
                .id(memberId)
                .name("테스트")
                .job(Job.toJob("백엔드"))
                .profileImage("profilePath")
                .onboarding(Onboarding.toOnboarding())
                .build();

        skillDto1 = ProjectSkillDto.builder()
                .projectId(1L)
                .projectName("project1")
                .projectDescription("project1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .skillType("소프트")
                .skillName("Communication")
                .skillCount(5L)
                .build();

        skillDto2 = ProjectSkillDto.builder()
                .projectId(2L)
                .projectName("project2")
                .projectDescription("project2")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .skillType("소프트")
                .skillName("Communication")
                .skillCount(5L)
                .build();

        skillDto3 = ProjectSkillDto.builder()
                .projectId(2L)
                .projectName("project2")
                .projectDescription("project2")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .skillType("하드")
                .skillName("Spring Boot")
                .skillCount(5L)
                .build();
    }

    @DisplayName("회원의 정보를 조회한다.")
    @Test
    void getMember() {
        // given
        when(memberRepository.findByIdWithJob(memberId)).thenReturn(Optional.of(mockMember));

        // when
        MemberResDto member = memberService.getMember(memberId);

        // then
        assertThat(member.name()).isEqualTo(mockMember.getName());
    }

    @DisplayName("온보딩 정보를 업데이트 한다.")
    @Test
    void updateOnboarding() {
        // given
        OnboardingReqDto req = OnboardingReqDto.builder()
                .name("아름 업데이트")
                .jobName("직무 업데이트")
                .build();

        when(memberRepository.findByIdWithJob(memberId)).thenReturn(Optional.of(mockMember));

        // when
        memberService.updateOnboarding(memberId, req);

        // then
        assertThat(mockMember.getName()).isEqualTo(req.name());
        assertThat(mockMember.getJob().getJobName()).isEqualTo(req.jobName());
    }

    @Test
    void updateMember() {
        // given
        MemberUpdateReqDto req = MemberUpdateReqDto.builder()
                .name("이름 업데이트")
                .jobName("직무 업데이트")
                .build();

        when(memberRepository.findByIdWithJob(memberId)).thenReturn(Optional.of(mockMember));

        // when
        memberService.updateMember(memberId, req, mock(MultipartFile.class));

        //then
        assertThat(mockMember.getName()).isEqualTo(req.name());
        verify(fileService, times(1)).saveProfileImage(any());
    }

    @DisplayName("회원의 포트폴리오를 생성할 때 필요한 값들을 반환한다.")
    @Test
    void getPortfolio() {
        // given
        List<ProjectSkillDto> skillDtoList = List.of(skillDto1, skillDto2, skillDto3);

        when(memberRepository.findByIdWithJob(memberId)).thenReturn(Optional.of(mockMember));
        when(retrospectiveSkillRepository.findProjectSkillsByMemberId(memberId)).thenReturn(
                skillDtoList);

        //when
        MemberPortfolioResDto portfolio = memberService.getPortfolio(memberId);

        //then
        assertThat(portfolio.name()).isEqualTo(mockMember.getName());
        assertThat(portfolio.projectSkillResDto().size()).isEqualTo(2);
    }
}