package com.skill.platform.social.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for a SKILL rating.
 * <p>
 * Returned by rating submission and listing endpoints to convey the
 * rating details along with the associated user and skill identifiers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {

    private UUID id;
    private UUID skillId;
    private UUID userId;
    private String userName;
    private Integer rating;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
}
