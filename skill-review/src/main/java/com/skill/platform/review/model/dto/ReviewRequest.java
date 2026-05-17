package com.skill.platform.review.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for submitting a skill review decision.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    /**
     * The review action: "approved" or "rejected".
     */
    @NotBlank(message = "Action must not be blank")
    @Pattern(regexp = "^(approved|rejected)$", message = "Action must be 'approved' or 'rejected'")
    private String action;

    /**
     * Optional reviewer comment explaining the decision.
     */
    private String comment;
}
