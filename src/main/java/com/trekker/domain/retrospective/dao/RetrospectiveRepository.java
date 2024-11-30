package com.trekker.domain.retrospective.dao;

import com.trekker.domain.retrospective.entity.Retrospective;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {

}
