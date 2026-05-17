package com.skill.platform.deploy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload returned after initiating or querying a SKILL deployment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployResponse {

    /**
     * Unique identifier of the deployment record.
     */
    private UUID deploymentId;

    /**
     * Current deployment status (pending, deploying, deployed, failed, rolled_back).
     */
    private String status;

    /**
     * The deployment target type (docker / k8s).
     */
    private String targetType;

    /**
     * Endpoint URL where the deployed SKILL is accessible.
     * Only populated when status is "deployed".
     */
    private String endpoint;

    /**
     * Estimated time (in seconds) until the deployment completes.
     * Populated for in-progress deployments.
     */
    private Integer estimatedTime;

    /**
     * Error message when the deployment has failed.
     */
    private String errorMessage;

    /**
     * Timestamp when the deployment was created.
     */
    private Instant createdAt;

    /**
     * Timestamp when the deployment completed (successfully or with failure).
     */
    private Instant completedAt;
}
