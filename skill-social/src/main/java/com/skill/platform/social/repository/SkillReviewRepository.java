package com.skill.platform.social.repository;

import com.skill.platform.social.model.SkillReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillReviewRepository extends JpaRepository<SkillReview, UUID> {

    Page<SkillReview> findBySkillId(UUID skillId, Pageable pageable);

    Page<SkillReview> findByReviewerId(UUID reviewerId, Pageable pageable);

    Optional<SkillReview> findTopBySkillIdOrderByCreatedAtDesc(UUID skillId);
}
