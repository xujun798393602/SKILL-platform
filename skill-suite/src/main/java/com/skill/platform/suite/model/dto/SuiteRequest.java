package com.skill.platform.suite.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating or updating a SKILL suite.
 */
@Data
public class SuiteRequest {

    @NotBlank(message = "Suite name is required")
    @Size(max = 200, message = "Suite name must not exceed 200 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @NotBlank(message = "Visibility is required")
    private String visibility;

    @NotNull(message = "Skill IDs are required")
    @Size(min = 2, max = 20, message = "A suite must contain between 2 and 20 skills")
    private List<UUID> skillIds;
}
