package com.skill.platform.core.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for SKILL full-text search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillSearchRequest {

    @NotBlank(message = "Search keyword must not be blank")
    private String keyword;

    @Min(value = 1, message = "Page number must be >= 1")
    @Builder.Default
    private int page = 1;

    @Min(value = 1, message = "Page size must be >= 1")
    @Builder.Default
    private int pageSize = 20;

    /**
     * Optional filter to restrict results to a specific skill type
     * (e.g. "personal", "shared", "system").
     */
    private String skillType;
}
