package com.skill.platform.social.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for SKILL share operations (F015).
 * <p>
 * Returned when generating a share link or querying share details.
 * When used for public access via token, the skill detail fields are populated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareResponse {

    /**
     * Unique identifier of the share record.
     */
    private UUID shareId;

    /**
     * The unique share token (UUID-based, 64 characters).
     */
    private String shareToken;

    /**
     * Full URL for accessing the shared skill.
     */
    private String shareUrl;

    /**
     * Share type (e.g. "public", "internal").
     */
    private String shareType;

    /**
     * Timestamp when this share link expires.
     */
    private Instant expiresAt;

    /**
     * Number of times this share link has been accessed.
     */
    private Integer accessCount;

    /**
     * Timestamp when the share was created.
     */
    private Instant createdAt;

    // ---- Skill detail fields (populated on public access) ----

    /**
     * The shared skill's ID.
     */
    private UUID skillId;

    /**
     * The shared skill's name.
     */
    private String skillName;

    /**
     * The shared skill's description.
     */
    private String skillDescription;

    /**
     * The shared skill's type.
     */
    private String skillType;

    /**
     * The shared skill's version.
     */
    private String skillVersion;

    /**
     * The shared skill's average rating.
     */
    private java.math.BigDecimal avgRating;

    /**
     * The shared skill's rating count.
     */
    private Integer ratingCount;
}
