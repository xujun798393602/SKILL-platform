package com.skill.platform.core.repository;

import com.skill.platform.core.model.SkillTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkillTagRepository extends JpaRepository<SkillTag, UUID> {
    List<SkillTag> findBySkillId(UUID skillId);
    void deleteBySkillId(UUID skillId);
}
