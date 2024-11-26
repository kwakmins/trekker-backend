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
            WHERE m.email =:email 
            """)
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("""
            SELECT m 
            FROM Member m 
            JOIN FETCH m.socialProvider s
            JOIN FETCH m.onboarding o
            WHERE s.provider =:provider AND s.providerId =:providerId
            """)
    Optional<Member> findByProviderAndProviderId(@Param("provider") String provider,
            @Param("providerId") String providerId);
}