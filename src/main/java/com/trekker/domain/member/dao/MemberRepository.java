package com.trekker.domain.member.dao;

import com.trekker.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("""
            SELECT m
            FROM Member m 
            JOIN FETCH m.socialProvider s 
            JOIN FETCH m.onboarding o
            WHERE m.id =:memberId 
            """)
    Optional<Member> findByIdWithSocialAndOnboarding(@Param("memberId") Long memberId);

    @Query("""
            SELECT m 
            FROM Member m 
            JOIN FETCH m.socialProvider s
            JOIN FETCH m.onboarding o
            WHERE s.provider =:provider AND s.providerId =:providerId
            """)
    Optional<Member> findByProviderAndProviderId(@Param("provider") String provider,
            @Param("providerId") String providerId);
    @Query("""
           SELECT m
           FROM Member m
           JOIN FETCH m.job
           LEFT JOIN FETCH m.projectList p
           WHERE m.id = :memberId
           AND (p IS NULL OR (p.isCompleted = false AND (:type IS NULL OR p.type = :type)))
           """)
    Optional<Member> findByIdWithProjectList(@Param("memberId") Long memberId, @Param("type") String type);
}