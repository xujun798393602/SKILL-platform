package com.skill.platform.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a system configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigRequest {

    /**
     * The new configuration value. Must not be blank.
     */
    @NotBlank(message = "Configuration value must not be blank")
    @Size(max = 10000, message = "Configuration value exceeds maximum length")
    private String configValue;

    /**
     * Optional updated description for the configuration item.
     */
    @Size(max = 500, message = "Description exceeds maximum length of 500 characters")
    private String description;
}
