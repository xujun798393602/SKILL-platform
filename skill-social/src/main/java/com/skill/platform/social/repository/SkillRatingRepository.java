package com.skill.platform.social.repository;

import com.skill.platform.social.model.SkillRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRatingRepository extends JpaRepository<SkillRating, UUID> {

    Page<SkillRating> findBySkillId(UUID skillId, Pageable pageable);

    Optional<SkillRating> findBySkillIdAndUserId(UUID skillId, UUID userId);

    boolean existsBySkillIdAndUserId(UUID skillId, UUID userId);

    Page<SkillRating> findByUserId(UUID userId, Pageable pageable);
}
