package com.skill.platform.review.service;

import com.skill.platform.auth.model.User;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.review.event.SkillReviewedEvent;
import com.skill.platform.review.model.dto.ReviewRequest;
import com.skill.platform.review.model.dto.ReviewResponse;
import com.skill.platform.social.model.SkillReview;
import com.skill.platform.social.repository.SkillReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for SKILL review workflow.
 * <p>
 * Handles reviewer permission validation, review record creation,
 * skill status transitions, and notification publishing.
 */
@Service
@Slf4j
public class ReviewService {

    private final SkillReviewRepository skillReviewRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReviewService(SkillReviewRepository skillReviewRepository,
                         SkillRepository skillRepository,
                         UserRepository userRepository,
                         ApplicationEventPublisher eventPublisher) {
        this.skillReviewRepository = skillReviewRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Submit a review decision for a skill.
     * <p>
     * Validates the reviewer has the required role, checks the skill is in
     * "pending_review" status, creates a review record, updates the skill status,
     * and publishes a notification event.
     *
     * @param skillId the ID of the skill being reviewed
     * @param request the review decision (action + comment)
     * @return the created review details
     */
    @Transactional
    public ReviewResponse submitReview(UUID skillId, ReviewRequest request) {
        // 1. Validate reviewer role
        String userId = UserContext.getUserId();
        if (!UserContext.hasRole("REVIEWER") && !UserContext.hasRole("ADMIN")) {
            throw new BusinessException("AUTH003", "Only REVIEWER or ADMIN can perform reviews", 403);
        }

        // 2. Find skill and validate status
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new BusinessException("SYSTEM001", "Skill not found: " + skillId, 404));

        if (!"pending_review".equals(skill.getStatus())) {
            throw new BusinessException("REVIEW003",
                    "Skill is not in pending_review status, current status: " + skill.getStatus(), 400);
        }

        // 3. Find reviewer user entity
        User reviewer = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException("AUTH001", "Reviewer user not found", 401));

        // 4. Create review record
        SkillReview review = SkillReview.builder()
                .skill(skill)
                .reviewer(reviewer)
                .action(request.getAction())
                .comment(request.getComment())
                .build();
        review = skillReviewRepository.save(review);

        // 5. Update skill status based on action
        if ("approved".equals(request.getAction())) {
            skill.setStatus("published");
        } else {
            skill.setStatus("rejected");
        }
        skillRepository.save(skill);

        log.info("Skill {} reviewed by user {} - action: {}", skillId, userId, request.getAction());

        // 6. Publish notification event
        eventPublisher.publishEvent(SkillReviewedEvent.builder()
                .skillId(skillId)
                .reviewerId(reviewer.getId())
                .action(request.getAction())
                .comment(request.getComment())
                .build());

        return toResponse(review);
    }

    /**
     * List reviews for a specific skill with pagination.
     *
     * @param skillId  the skill ID
     * @param pageable pagination parameters
     * @return paginated list of reviews
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> listReviewsBySkill(UUID skillId, Pageable pageable) {
        return skillReviewRepository.findBySkillId(skillId, pageable)
                .map(this::toResponse);
    }

    /**
     * List all reviews with pagination (admin only).
     *
     * @param pageable pagination parameters
     * @return paginated list of all reviews
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> listAllReviews(Pageable pageable) {
        return skillReviewRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private ReviewResponse toResponse(SkillReview review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .skillId(review.getSkill().getId())
                .skillName(review.getSkill().getName())
                .reviewerId(review.getReviewer().getId())
                .reviewerName(review.getReviewer().getName())
                .action(review.getAction())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
