package com.skill.platform.core.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for setting or updating a version tag.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionTagRequest {

    @NotBlank(message = "Tag must not be blank")
    @Size(max = 50, message = "Tag must not exceed 50 characters")
    private String tag;
}
