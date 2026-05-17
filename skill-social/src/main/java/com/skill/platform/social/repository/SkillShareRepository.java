package com.skill.platform.social.repository;

import com.skill.platform.social.model.SkillShare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillShareRepository extends JpaRepository<SkillShare, UUID> {

    Optional<SkillShare> findByShareToken(String shareToken);

    Page<SkillShare> findByUserId(UUID userId, Pageable pageable);
}
