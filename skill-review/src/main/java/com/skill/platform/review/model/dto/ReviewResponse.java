package com.skill.platform.review.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO representing a completed skill review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private UUID id;
    private UUID skillId;
    private String skillName;
    private UUID reviewerId;
    private String reviewerName;
    private String action;
    private String comment;
    private Instant createdAt;
}
