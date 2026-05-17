package com.skill.platform.core.repository;

import com.skill.platform.core.model.SkillVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillVersionRepository extends JpaRepository<SkillVersion, UUID> {
    List<SkillVersion> findBySkillIdOrderByVersionDesc(UUID skillId);
    Optional<SkillVersion> findBySkillIdAndVersion(UUID skillId, String version);
    Optional<SkillVersion> findBySkillIdAndIsActiveTrue(UUID skillId);
    Page<SkillVersion> findBySkillIdOrderByCreatedAtDesc(UUID skillId, Pageable pageable);
}
