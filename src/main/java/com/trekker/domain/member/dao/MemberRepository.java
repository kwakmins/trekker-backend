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
           LEFT JOIN FETCH m.job
           WHERE m.id =:memberId
           """)
    Optional<Member> findByIdWithJob(@Param("memberId") Long memberId);
}