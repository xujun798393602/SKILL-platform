package com.skill.platform.graph.repository;

import com.skill.platform.graph.model.SkillRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRelationRepository extends JpaRepository<SkillRelation, UUID> {
    List<SkillRelation> findBySourceSkillId(UUID sourceSkillId);
    List<SkillRelation> findByTargetSkillId(UUID targetSkillId);
    Optional<SkillRelation> findBySourceSkillIdAndTargetSkillIdAndRelationType(UUID sourceId, UUID targetId, String type);
}
