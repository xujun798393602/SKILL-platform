package com.skill.platform.deploy.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.deploy.model.dto.DeployRequest;
import com.skill.platform.deploy.model.dto.DeployResponse;
import com.skill.platform.deploy.service.DeployService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for SKILL deployment operations (F008).
 * <p>
 * Exposes endpoints for triggering deployments, querying deployment status,
 * rolling back deployments, and listing deployment history for a skill.
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DeployController {

    private final DeployService deployService;

    public DeployController(DeployService deployService) {
        this.deployService = deployService;
    }

    /**
     * Trigger a new deployment for the specified skill.
     *
     * @param skillId the skill to deploy
     * @param request the deployment configuration (target type, optional config)
     * @return the initial deployment status (202 Accepted)
     */
    @PostMapping("/skills/{skillId}/deploy")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<DeployResponse> deploy(
            @PathVariable UUID skillId,
            @Valid @RequestBody DeployRequest request) {
        log.info("Deployment request received: skillId={}, targetType={}", skillId, request.getTargetType());
        DeployResponse response = deployService.deploy(skillId, request);
        return ApiResponse.success("Deployment initiated", response);
    }

    /**
     * Get the current status of a deployment.
     *
     * @param deploymentId the deployment ID
     * @return the deployment status details
     */
    @GetMapping("/deployments/{deploymentId}")
    public ApiResponse<DeployResponse> getDeployment(@PathVariable UUID deploymentId) {
        return ApiResponse.success(deployService.getDeployment(deploymentId));
    }

    /**
     * Rollback a deployment.
     *
     * @param deploymentId the deployment to roll back
     * @return the updated deployment status
     */
    @PostMapping("/deployments/{deploymentId}/rollback")
    public ApiResponse<DeployResponse> rollback(@PathVariable UUID deploymentId) {
        log.info("Rollback request received: deploymentId={}", deploymentId);
        DeployResponse response = deployService.rollback(deploymentId);
        return ApiResponse.success("Deployment rolled back", response);
    }

    /**
     * List all deployments for a skill.
     *
     * @param skillId  the skill ID
     * @param pageable pagination parameters
     * @return a paginated list of deployment responses
     */
    @GetMapping("/skills/{skillId}/deployments")
    public ApiResponse<Page<DeployResponse>> listDeployments(
            @PathVariable UUID skillId,
            Pageable pageable) {
        return ApiResponse.success(deployService.listDeployments(skillId, pageable));
    }
}
