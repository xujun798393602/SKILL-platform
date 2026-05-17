package com.skill.platform.deploy.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for triggering a SKILL deployment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployRequest {

    /**
     * Deployment target type: "docker" or "k8s".
     */
    @NotBlank(message = "Target type is required")
    @Pattern(regexp = "^(docker|k8s)$", message = "Target type must be 'docker' or 'k8s'")
    private String targetType;

    /**
     * Optional deployment configuration as a JSON string.
     * <p>
     * For Docker targets this may contain image name, port mappings, environment
     * variables, etc. For K8s targets it may contain namespace, replicas, resource
     * limits, etc.
     */
    private String config;
}
