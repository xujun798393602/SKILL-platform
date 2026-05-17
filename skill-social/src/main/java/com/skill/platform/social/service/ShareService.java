package com.skill.platform.social.service;

import com.skill.platform.auth.model.User;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.social.model.SkillShare;
import com.skill.platform.social.model.dto.ShareResponse;
import com.skill.platform.social.repository.SkillShareRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing SKILL share operations (F015 - SKILL分享服务).
 * <p>
 * Handles share link generation, public access validation, and user share listing.
 * Validates that the skill is published before allowing sharing, generates unique
 * share tokens, enforces expiration policies, and tracks access counts.
 */
@Service
@Slf4j
public class ShareService {

    private static final int SHARE_EXPIRY_DAYS = 7;
    private static final Set<String> SHAREABLE_STATUSES = Set.of("published");

    private final SkillShareRepository shareRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Value("${app.share.base-url:http://localhost:8080/api/v1/shared}")
    private String shareBaseUrl;

    public ShareService(SkillShareRepository shareRepository,
                        SkillRepository skillRepository,
                        UserRepository userRepository) {
        this.shareRepository = shareRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    /**
     * Generate a share link for a published skill.
     * <p>
     * Validates the skill exists and is in "published" status, generates a unique
     * 64-character share token, and sets expiration to 7 days from now.
     *
     * @param skillId the ID of the skill to share
     * @return the share response containing token, URL, and expiration
     * @throws BusinessException if the skill is not found or not published
     */
    @Transactional
    public ShareResponse createShare(UUID skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM001));

        if (!SHAREABLE_STATUSES.contains(skill.getStatus())) {
            throw new BusinessException("SHARE003",
                    "Skill must be in 'published' status to share, current: " + skill.getStatus(),
                    400);
        }

        User currentUser = resolveCurrentUser();

        String shareToken = generateShareToken();
        Instant expiresAt = Instant.now().plus(SHARE_EXPIRY_DAYS, ChronoUnit.DAYS);

        SkillShare share = SkillShare.builder()
                .skill(skill)
                .user(currentUser)
                .shareToken(shareToken)
                .shareType("public")
                .expiresAt(expiresAt)
                .accessCount(0)
                .build();

        share = shareRepository.save(share);
        log.info("Share created: id={}, skillId={}, token={}..., expiresAt={}",
                share.getId(), skillId, shareToken.substring(0, 8), expiresAt);

        return ShareResponse.builder()
                .shareId(share.getId())
                .shareToken(share.getShareToken())
                .shareUrl(shareBaseUrl + "/" + shareToken)
                .shareType(share.getShareType())
                .expiresAt(share.getExpiresAt())
                .accessCount(share.getAccessCount())
                .createdAt(share.getCreatedAt())
                .build();
    }

    /**
     * Access a shared skill by its token.
     * <p>
     * Validates the token exists and has not expired, increments the access count,
     * and returns the skill details. This is a public endpoint that does not require
     * authentication.
     *
     * @param token the share token
     * @return the share response with skill details
     * @throws BusinessException if the token is not found or expired
     */
    @Transactional
    public ShareResponse accessShare(String token) {
        SkillShare share = shareRepository.findByShareToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHARE002));

        if (Instant.now().isAfter(share.getExpiresAt())) {
            throw new BusinessException(ErrorCode.SHARE001);
        }

        // Increment access count
        share.setAccessCount(share.getAccessCount() + 1);
        share = shareRepository.save(share);

        Skill skill = share.getSkill();
        log.info("Share accessed: token={}..., skillId={}, accessCount={}",
                token.substring(0, Math.min(8, token.length())),
                skill.getId(), share.getAccessCount());

        return ShareResponse.builder()
                .shareId(share.getId())
                .shareToken(share.getShareToken())
                .shareType(share.getShareType())
                .expiresAt(share.getExpiresAt())
                .accessCount(share.getAccessCount())
                .createdAt(share.getCreatedAt())
                .skillId(skill.getId())
                .skillName(skill.getName())
                .skillDescription(skill.getDescription())
                .skillType(skill.getSkillType())
                .skillVersion(skill.getVersion())
                .avgRating(skill.getAvgRating())
                .ratingCount(skill.getRatingCount())
                .build();
    }

    /**
     * List the current user's share links with pagination.
     *
     * @param pageable pagination parameters
     * @return a paginated list of share responses
     */
    @Transactional(readOnly = true)
    public PageResponse<ShareResponse> listMyShares(Pageable pageable) {
        User currentUser = resolveCurrentUser();

        Page<ShareResponse> page = shareRepository.findByUserId(currentUser.getId(), pageable)
                .map(this::toResponse);

        return PageResponse.of(page);
    }

    /**
     * Resolve the current authenticated user from the UserContext.
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
     * Generate a unique 64-character share token.
     * <p>
     * Combines two UUIDs (with hyphens removed) to produce a 64-character hex string.
     * The token is verified to be unique before returning.
     *
     * @return a unique 64-character share token
     */
    private String generateShareToken() {
        String token;
        int attempts = 0;
        do {
            token = UUID.randomUUID().toString().replace("-", "")
                    + UUID.randomUUID().toString().replace("-", "");
            attempts++;
            if (attempts > 5) {
                log.warn("Share token generation required {} attempts", attempts);
            }
        } while (shareRepository.findByShareToken(token).isPresent());

        return token;
    }

    /**
     * Convert a SkillShare entity to a ShareResponse DTO.
     */
    private ShareResponse toResponse(SkillShare share) {
        Skill skill = share.getSkill();
        return ShareResponse.builder()
                .shareId(share.getId())
                .shareToken(share.getShareToken())
                .shareUrl(shareBaseUrl + "/" + share.getShareToken())
                .shareType(share.getShareType())
                .expiresAt(share.getExpiresAt())
                .accessCount(share.getAccessCount())
                .createdAt(share.getCreatedAt())
                .skillId(skill.getId())
                .skillName(skill.getName())
                .skillDescription(skill.getDescription())
                .skillType(skill.getSkillType())
                .skillVersion(skill.getVersion())
                .avgRating(skill.getAvgRating())
                .ratingCount(skill.getRatingCount())
                .build();
    }
}
