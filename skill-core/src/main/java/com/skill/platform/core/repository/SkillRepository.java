package com.skill.platform.core.repository;

import com.skill.platform.core.model.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID>, JpaSpecificationExecutor<Skill> {
    Page<Skill> findByOwnerId(UUID ownerId, Pageable pageable);
    Page<Skill> findByStatus(String status, Pageable pageable);
    Page<Skill> findBySkillTypeAndStatus(String skillType, String status, Pageable pageable);
    Page<Skill> findByCategoryAndStatus(String category, String status, Pageable pageable);
    Optional<Skill> findByIdAndStatus(UUID id, String status);
    long countByStatus(String status);
    long countByOwnerId(UUID ownerId);
}
