package com.skill.platform.deploy.repository;

import com.skill.platform.deploy.model.Deployment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, UUID> {
    Page<Deployment> findBySkillId(UUID skillId, Pageable pageable);
    Page<Deployment> findByStatus(String status, Pageable pageable);
    Optional<Deployment> findTopBySkillIdOrderByCreatedAtDesc(UUID skillId);
}
