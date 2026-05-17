package com.skill.platform.social.service;

import com.skill.platform.auth.model.User;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.core.model.DownloadLog;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.repository.DownloadLogRepository;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.social.model.SkillRating;
import com.skill.platform.social.model.dto.RatingRequest;
import com.skill.platform.social.model.dto.RatingResponse;
import com.skill.platform.social.repository.SkillRatingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Service for managing SKILL ratings (F013 - SKILL评价服务).
 * <p>
 * Handles rating submission with validation (download check, duplicate check),
 * paginated listing by skill or by current user, and automatic updates to the
 * Skill's aggregate rating fields (avgRating, ratingCount).
 */
@Service
@Slf4j
public class RatingService {

    private final SkillRatingRepository skillRatingRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final DownloadLogRepository downloadLogRepository;

    public RatingService(SkillRatingRepository skillRatingRepository,
                         SkillRepository skillRepository,
                         UserRepository userRepository,
                         DownloadLogRepository downloadLogRepository) {
        this.skillRatingRepository = skillRatingRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.downloadLogRepository = downloadLogRepository;
    }

    /**
     * Submit a rating for a SKILL.
     * <p>
     * Business rules:
     * <ol>
     *   <li>The target SKILL must exist.</li>
     *   <li>The current user must have downloaded the SKILL (DownloadLog record exists).</li>
     *   <li>The current user must not have already rated the SKILL (unique constraint on skill_id + user_id).</li>
     *   <li>Rating score must be between 1 and 5.</li>
     *   <li>Comment must not exceed 500 characters.</li>
     * </ol>
     * After persisting the rating, the Skill's avgRating and ratingCount are
     * updated using the incremental formula:
     * <pre>
     *   newAvgRating = (oldAvgRating * oldCount + newRating) / (oldCount + 1)
     *   newCount     = oldCount + 1
     * </pre>
     *
     * @param skillId the ID of the SKILL to rate
     * @param request the rating request containing score and optional comment
     * @return the created rating response
     */
    @Transactional
    public RatingResponse submitRating(UUID skillId, RatingRequest request) {
        // 1. Validate Skill exists
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM001));

        // 2. Resolve current user
        User user = resolveCurrentUser();

        // 3. Check user has downloaded this skill
        boolean hasDownloaded = downloadLogRepository.existsBySkillIdAndUserId(skillId, user.getId());
        if (!hasDownloaded) {
            throw new BusinessException("RATING003",
                    "You must download the SKILL before submitting a rating", 403);
        }

        // 4. Check no duplicate rating
        if (skillRatingRepository.existsBySkillIdAndUserId(skillId, user.getId())) {
            throw new BusinessException(ErrorCode.RATING002);
        }

        // 5. Create and persist the rating
        SkillRating rating = SkillRating.builder()
                .skill(skill)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        rating = skillRatingRepository.save(rating);
        log.info("Rating submitted: id={}, skillId={}, userId={}, score={}",
                rating.getId(), skillId, user.getId(), request.getRating());

        // 6. Update Skill aggregate rating
        updateSkillRating(skill, request.getRating());

        return toResponse(rating);
    }

    /**
     * List all ratings for a SKILL, ordered by creation time descending.
     *
     * @param skillId  the SKILL ID
     * @param pageable pagination parameters
     * @return a page of rating responses
     */
    @Transactional(readOnly = true)
    public Page<RatingResponse> listRatingsBySkill(UUID skillId, Pageable pageable) {
        // Validate skill exists
        if (!skillRepository.existsById(skillId)) {
            throw new BusinessException(ErrorCode.SYSTEM001);
        }

        return skillRatingRepository.findBySkillId(skillId, pageable)
                .map(this::toResponse);
    }

    /**
     * List all ratings submitted by the current authenticated user.
     *
     * @param pageable pagination parameters
     * @return a page of rating responses
     */
    @Transactional(readOnly = true)
    public Page<RatingResponse> listMyRatings(Pageable pageable) {
        User user = resolveCurrentUser();
        return skillRatingRepository.findByUserId(user.getId(), pageable)
                .map(this::toResponse);
    }

    /**
     * Update the Skill's average rating and rating count using the incremental formula.
     *
     * @param skill      the Skill entity to update
     * @param newRating  the new rating score to incorporate
     */
    private void updateSkillRating(Skill skill, int newRating) {
        BigDecimal oldAvg = skill.getAvgRating() != null ? skill.getAvgRating() : BigDecimal.ZERO;
        int oldCount = skill.getRatingCount() != null ? skill.getRatingCount() : 0;

        // newAvgRating = (oldAvg * oldCount + newRating) / (oldCount + 1)
        BigDecimal totalScore = oldAvg.multiply(BigDecimal.valueOf(oldCount))
                .add(BigDecimal.valueOf(newRating));
        BigDecimal newAvg = totalScore.divide(BigDecimal.valueOf(oldCount + 1), 2, RoundingMode.HALF_UP);
        int newCount = oldCount + 1;

        skill.setAvgRating(newAvg);
        skill.setRatingCount(newCount);
        skillRepository.save(skill);

        log.debug("Updated skill {} rating: avg={}, count={}", skill.getId(), newAvg, newCount);
    }

    /**
     * Resolve the current authenticated user from UserContext.
     *
     * @return the User entity
     * @throws BusinessException if user is not authenticated or not found
     */
    private User resolveCurrentUser() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.AUTH001);
        }
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH001));
    }

    /**
     * Convert a SkillRating entity to a RatingResponse DTO.
     *
     * @param rating the SkillRating entity
     * @return the RatingResponse DTO
     */
    private RatingResponse toResponse(SkillRating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .skillId(rating.getSkill().getId())
                .userId(rating.getUser().getId())
                .userName(rating.getUser().getName())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}
