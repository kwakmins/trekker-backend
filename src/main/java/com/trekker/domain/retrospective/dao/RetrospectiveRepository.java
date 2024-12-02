package com.trekker.domain.retrospective.dao;

import com.trekker.domain.retrospective.entity.Retrospective;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {

   @Query("""
          SELECT r
          FROM Retrospective r
          JOIN FETCH r.retrospectiveSkillList sl
          JOIN FETCH sl.skill
          WHERE r.id =:retrospectiveId
          """)
   Optional<Retrospective> findByIdWithSkillList(@Param("retrospectiveId") Long retrospectiveId);
}
