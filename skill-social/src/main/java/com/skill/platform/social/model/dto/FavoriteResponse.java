package com.skill.platform.social.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO returned when listing a user's favorites.
 * <p>
 * Contains the favorite entry id, the favorited-at timestamp, and a summary
 * of the associated Skill so that the client can render a card without a
 * second request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {

    /** The favorite entry ID (primary key of skill_favorites). */
    private UUID favoriteId;

    /** The timestamp when the favorite was created. */
    private Instant createdAt;

    // ---- Skill summary fields ----

    private UUID skillId;
    private String skillName;
    private String skillDescription;
    private String skillType;
    private String category;
    private String skillStatus;
    private String ownerName;
}
