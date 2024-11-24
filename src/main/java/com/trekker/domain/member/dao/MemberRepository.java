package com.trekker.domain.member.dao;

import com.trekker.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m join fetch m.socialProvider s where m.email =:email")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("select m from Member m join fetch m.socialProvider s where s.provider =:provider and s.providerId =:providerId")
    Optional<Member> findByProviderAndProviderId(@Param("provider") String provider,
            @Param("providerId") String providerId);
}
