package com.skill.platform.social.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for submitting a SKILL rating.
 * <p>
 * Used by POST /api/v1/skills/{skillId}/ratings to accept the user's
 * rating score and optional comment for a downloaded SKILL.
 */
@Data
public class RatingRequest {

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;
}
