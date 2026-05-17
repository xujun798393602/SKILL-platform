package com.skill.platform.social.service;

import com.skill.platform.auth.model.User;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.social.model.SkillFavorite;
import com.skill.platform.social.model.dto.FavoriteResponse;
import com.skill.platform.social.repository.SkillFavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer for SKILL favorite operations (F014).
 * <p>
 * Supports adding, removing, and listing favorites with the following
 * business rules:
 * <ul>
 *   <li>A user may have at most 100 favorites.</li>
 *   <li>Duplicate favorites are rejected (409 Conflict).</li>
 *   <li>Removing a non-existent favorite is rejected (404 Not Found).</li>
 *   <li>The target Skill must exist.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private static final int MAX_FAVORITES_PER_USER = 100;

    private final SkillFavoriteRepository favoriteRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    /**
     * Add a skill to the current user's favorites.
     *
     * @param skillId the ID of the skill to favorite
     * @throws BusinessException if the skill does not exist, the user already
     *                           favorited it, or the 100-favorite cap is reached
     */
    @Transactional
    public void addFavorite(UUID skillId) {
        UUID userId = getCurrentUserId();

        // Validate skill exists
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM001));

        // Check duplicate
        if (favoriteRepository.existsBySkillIdAndUserId(skillId, userId)) {
            throw new BusinessException(ErrorCode.FAVORITE001);
        }

        // Check cap
        long count = favoriteRepository.countByUserId(userId);
        if (count >= MAX_FAVORITES_PER_USER) {
            throw new BusinessException(
                    "FAVORITE003",
                    "User has reached the maximum of " + MAX_FAVORITES_PER_USER + " favorites",
                    400
            );
        }

        User user = userRepository.getReferenceById(userId);
        SkillFavorite favorite = SkillFavorite.builder()
                .skill(skill)
                .user(user)
                .build();
        favoriteRepository.save(favorite);

        log.info("User {} added skill {} to favorites", userId, skillId);
    }

    /**
     * Remove a skill from the current user's favorites.
     *
     * @param skillId the ID of the skill to unfavorite
     * @throws BusinessException if the favorite entry does not exist
     */
    @Transactional
    public void removeFavorite(UUID skillId) {
        UUID userId = getCurrentUserId();

        SkillFavorite favorite = favoriteRepository.findBySkillIdAndUserId(skillId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FAVORITE002));

        favoriteRepository.delete(favorite);
        log.info("User {} removed skill {} from favorites", userId, skillId);
    }

    /**
     * List the current user's favorites with pagination.
     *
     * @param pageable pagination parameters
     * @return a paginated list of {@link FavoriteResponse} entries
     */
    @Transactional(readOnly = true)
    public PageResponse<FavoriteResponse> listFavorites(Pageable pageable) {
        UUID userId = getCurrentUserId();

        Page<SkillFavorite> page = favoriteRepository.findByUserId(userId, pageable);

        PageResponse<FavoriteResponse> response = PageResponse.of(page.map(this::toResponse));
        log.debug("User {} listing favorites, page={}, total={}", userId, page.getNumber(), page.getTotalElements());
        return response;
    }

    // ---- helpers ----

    private FavoriteResponse toResponse(SkillFavorite fav) {
        Skill skill = fav.getSkill();
        return FavoriteResponse.builder()
                .favoriteId(fav.getId())
                .createdAt(fav.getCreatedAt())
                .skillId(skill.getId())
                .skillName(skill.getName())
                .skillDescription(skill.getDescription())
                .skillType(skill.getSkillType())
                .category(skill.getCategory())
                .skillStatus(skill.getStatus())
                .ownerName(skill.getOwner() != null ? skill.getOwner().getName() : null)
                .build();
    }

    private UUID getCurrentUserId() {
        String userIdStr = UserContext.getUserId();
        if (userIdStr == null) {
            throw new BusinessException(ErrorCode.AUTH001);
        }
        return UUID.fromString(userIdStr);
    }
}
