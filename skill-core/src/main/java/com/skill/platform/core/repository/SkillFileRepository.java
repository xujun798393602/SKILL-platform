package com.skill.platform.core.repository;

import com.skill.platform.core.model.SkillFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkillFileRepository extends JpaRepository<SkillFile, UUID> {
    List<SkillFile> findBySkillId(UUID skillId);
}
