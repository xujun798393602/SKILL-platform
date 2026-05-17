package com.skill.platform.graph.repository;

import com.skill.platform.graph.model.PositionSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionSkillRepository extends JpaRepository<PositionSkill, UUID> {
    List<PositionSkill> findByPosition(String position);
    Optional<PositionSkill> findByPositionAndSkillId(String position, UUID skillId);
}
