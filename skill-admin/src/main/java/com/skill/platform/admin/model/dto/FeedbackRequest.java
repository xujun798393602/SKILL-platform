package com.skill.platform.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting user feedback.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    /**
     * Feedback title. Must not be blank, max 200 characters.
     */
    @NotBlank(message = "Title must not be blank")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    /**
     * Feedback content. Must not be blank, 1-1000 characters.
     */
    @NotBlank(message = "Content must not be blank")
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    private String content;

    /**
     * Feedback category (e.g. "bug", "feature", "other"). Max 50 characters.
     */
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;
}
