package com.skill.platform.social.repository;

import com.skill.platform.social.model.SkillFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillFavoriteRepository extends JpaRepository<SkillFavorite, UUID> {

    Page<SkillFavorite> findByUserId(UUID userId, Pageable pageable);

    Optional<SkillFavorite> findBySkillIdAndUserId(UUID skillId, UUID userId);

    boolean existsBySkillIdAndUserId(UUID skillId, UUID userId);

    long countByUserId(UUID userId);
}
