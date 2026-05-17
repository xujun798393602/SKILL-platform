package com.skill.platform.suite.repository;

import com.skill.platform.suite.model.Suite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SuiteRepository extends JpaRepository<Suite, UUID> {
    Page<Suite> findByOwnerId(UUID ownerId, Pageable pageable);
    Page<Suite> findByStatus(String status, Pageable pageable);
    long countByOwnerId(UUID ownerId);
}
