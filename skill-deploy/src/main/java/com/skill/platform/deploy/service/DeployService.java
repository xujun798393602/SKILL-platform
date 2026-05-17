package com.skill.platform.deploy.service;

import com.skill.platform.auth.model.User;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.deploy.model.Deployment;
import com.skill.platform.deploy.model.dto.DeployRequest;
import com.skill.platform.deploy.model.dto.DeployResponse;
import com.skill.platform.deploy.repository.DeploymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service for managing SKILL deployments (F008 - SKILL一键部署服务).
 * <p>
 * Handles deployment lifecycle: validation, creation, async execution with
 * timeout detection, status queries, and rollback operations. Supports both
 * Docker and Kubernetes deployment targets.
 */
@Service
@Slf4j
public class DeployService {

    private static final Set<String> DEPLOYABLE_STATUSES = Set.of("validated", "published");
    private static final int DEPLOY_TIMEOUT_SECONDS = 120;
    private static final Set<String> IN_PROGRESS_STATUSES = Set.of("pending", "deploying");

    private final DeploymentRepository deploymentRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public DeployService(DeploymentRepository deploymentRepository,
                         SkillRepository skillRepository,
                         UserRepository userRepository) {
        this.deploymentRepository = deploymentRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    /**
     * Trigger a new deployment for the given skill.
     * <p>
     * Validates the skill exists and is in a deployable status, creates a
     * deployment record, then kicks off the async deploy simulation.
     *
     * @param skillId the ID of the skill to deploy
     * @param request the deployment request containing target type and optional config
     * @return the initial deployment response with status "pending"
     */
    @Transactional
    public DeployResponse deploy(UUID skillId, DeployRequest request) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM001));

        if (!DEPLOYABLE_STATUSES.contains(skill.getStatus())) {
            throw new BusinessException("DEPLOY001",
                    "Skill must be in 'validated' or 'published' status to deploy, current: " + skill.getStatus(),
                    400);
        }

        // Check for an existing in-progress deployment
        deploymentRepository.findTopBySkillIdOrderByCreatedAtDesc(skillId)
                .ifPresent(existing -> {
                    if (IN_PROGRESS_STATUSES.contains(existing.getStatus())) {
                        throw new BusinessException(ErrorCode.DEPLOY003);
                    }
                });

        User currentUser = resolveCurrentUser();

        Deployment deployment = Deployment.builder()
                .skill(skill)
                .version(skill.getVersion())
                .status("pending")
                .targetType(request.getTargetType())
                .config(request.getConfig())
                .deployedBy(currentUser)
                .deployedAt(Instant.now())
                .build();

        deployment = deploymentRepository.save(deployment);
        log.info("Deployment created: id={}, skillId={}, targetType={}",
                deployment.getId(), skillId, request.getTargetType());

        // Trigger async deploy simulation
        simulateDeploy(deployment.getId());

        return DeployResponse.builder()
                .deploymentId(deployment.getId())
                .status(deployment.getStatus())
                .targetType(deployment.getTargetType())
                .estimatedTime(DEPLOY_TIMEOUT_SECONDS)
                .createdAt(deployment.getDeployedAt())
                .build();
    }

    /**
     * Get the current status of a deployment.
     *
     * @param deploymentId the deployment ID
     * @return the deployment response
     */
    @Transactional(readOnly = true)
    public DeployResponse getDeployment(UUID deploymentId) {
        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM001));

        return toResponse(deployment);
    }

    /**
     * Rollback a deployment.
     * <p>
     * Sets the deployment status to "rolled_back" and, if a previous successful
     * deployment exists for the same skill, restores its endpoint.
     *
     * @param deploymentId the deployment to roll back
     * @return the updated deployment response
     */
    @Transactional
    public DeployResponse rollback(UUID deploymentId) {
        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM001));

        if ("pending".equals(deployment.getStatus()) || "deploying".equals(deployment.getStatus())) {
            throw new BusinessException("DEPLOY001",
                    "Cannot rollback a deployment that is still in progress", 409);
        }

        if ("rolled_back".equals(deployment.getStatus())) {
            throw new BusinessException("DEPLOY001",
                    "Deployment is already rolled back", 409);
        }

        // Attempt to restore the endpoint from the previous successful deployment
        String restoredEndpoint = null;
        Page<Deployment> history = deploymentRepository.findBySkillId(
                deployment.getSkill().getId(),
                Pageable.ofSize(10));
        for (Deployment candidate : history.getContent()) {
            if (!candidate.getId().equals(deploymentId)
                    && "deployed".equals(candidate.getStatus())
                    && candidate.getEndpoint() != null) {
                restoredEndpoint = candidate.getEndpoint();
                break;
            }
        }

        deployment.setStatus("rolled_back");
        deployment.setCompletedAt(Instant.now());
        if (restoredEndpoint != null) {
            deployment.setEndpoint(restoredEndpoint);
        }
        deployment = deploymentRepository.save(deployment);

        log.info("Deployment rolled back: id={}, restoredEndpoint={}", deploymentId, restoredEndpoint);
        return toResponse(deployment);
    }

    /**
     * List all deployments for a skill, ordered by creation time descending.
     *
     * @param skillId  the skill ID
     * @param pageable pagination parameters
     * @return a page of deployment responses
     */
    @Transactional(readOnly = true)
    public Page<DeployResponse> listDeployments(UUID skillId, Pageable pageable) {
        return deploymentRepository.findBySkillId(skillId, pageable)
                .map(this::toResponse);
    }

    /**
     * Simulate the deployment process asynchronously.
     * <p>
     * Transitions the deployment through pending -> deploying -> deployed/failed.
     * Includes a simulated timeout check: if the simulated deploy takes longer
     * than 120 seconds, the deployment fails with a timeout error.
     *
     * @param deploymentId the deployment to process
     */
    @Async
    @Transactional
    public void simulateDeploy(UUID deploymentId) {
        try {
            Deployment deployment = deploymentRepository.findById(deploymentId).orElse(null);
            if (deployment == null) {
                log.error("Deployment not found for async simulation: {}", deploymentId);
                return;
            }

            // Transition to "deploying"
            deployment.setStatus("deploying");
            deploymentRepository.save(deployment);
            log.info("Deployment {} status -> deploying", deploymentId);

            // Simulate deploy work (random duration between 5-15 seconds)
            int simulatedSeconds = ThreadLocalRandom.current().nextInt(5, 16);
            Thread.sleep(simulatedSeconds * 1000L);

            // Reload to check for concurrent rollback or cancellation
            deployment = deploymentRepository.findById(deploymentId).orElse(null);
            if (deployment == null || !"deploying".equals(deployment.getStatus())) {
                log.info("Deployment {} was cancelled or modified externally, skipping", deploymentId);
                return;
            }

            // Check timeout
            long elapsedSeconds = Instant.now().getEpochSecond() - deployment.getDeployedAt().getEpochSecond();
            if (elapsedSeconds > DEPLOY_TIMEOUT_SECONDS) {
                deployment.setStatus("failed");
                deployment.setErrorMessage("Deployment timed out after " + DEPLOY_TIMEOUT_SECONDS + " seconds");
                deployment.setCompletedAt(Instant.now());
                deploymentRepository.save(deployment);
                log.warn("Deployment {} timed out after {}s", deploymentId, elapsedSeconds);
                return;
            }

            // Simulate a 10% random failure rate
            if (ThreadLocalRandom.current().nextInt(10) == 0) {
                deployment.setStatus("failed");
                deployment.setErrorMessage("Simulated deployment failure: container health check did not pass");
                deployment.setCompletedAt(Instant.now());
                deploymentRepository.save(deployment);
                log.warn("Deployment {} failed (simulated)", deploymentId);
                return;
            }

            // Success - generate endpoint
            String endpoint = generateEndpoint(deployment);
            deployment.setStatus("deployed");
            deployment.setEndpoint(endpoint);
            deployment.setCompletedAt(Instant.now());
            deploymentRepository.save(deployment);

            log.info("Deployment {} completed successfully, endpoint={}", deploymentId, endpoint);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Deployment simulation interrupted for {}", deploymentId, e);
            markFailed(deploymentId, "Deployment interrupted: " + e.getMessage());
        } catch (Exception e) {
            log.error("Deployment simulation failed for {}", deploymentId, e);
            markFailed(deploymentId, "Deployment failed: " + e.getMessage());
        }
    }

    /**
     * Generate a simulated endpoint URL based on the deployment target type.
     */
    private String generateEndpoint(Deployment deployment) {
        int port = 8000 + ThreadLocalRandom.current().nextInt(1000);
        if ("k8s".equals(deployment.getTargetType())) {
            return String.format("http://skill-%s.default.svc.cluster.local:%d",
                    deployment.getSkill().getId().toString().substring(0, 8), port);
        }
        return String.format("http://localhost:%d/skills/%s",
                port, deployment.getSkill().getId().toString().substring(0, 8));
    }

    /**
     * Mark a deployment as failed with the given error message.
     */
    private void markFailed(UUID deploymentId, String errorMessage) {
        deploymentRepository.findById(deploymentId).ifPresent(d -> {
            d.setStatus("failed");
            d.setErrorMessage(errorMessage);
            d.setCompletedAt(Instant.now());
            deploymentRepository.save(d);
        });
    }

    /**
     * Resolve the current authenticated user from the UserContext.
     */
    private User resolveCurrentUser() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.AUTH001);
        }
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH001));
    }

    /**
     * Convert a Deployment entity to a DeployResponse DTO.
     */
    private DeployResponse toResponse(Deployment deployment) {
        Integer estimatedTime = null;
        if (IN_PROGRESS_STATUSES.contains(deployment.getStatus()) && deployment.getDeployedAt() != null) {
            long elapsed = Instant.now().getEpochSecond() - deployment.getDeployedAt().getEpochSecond();
            estimatedTime = Math.max(0, DEPLOY_TIMEOUT_SECONDS - (int) elapsed);
        }

        return DeployResponse.builder()
                .deploymentId(deployment.getId())
                .status(deployment.getStatus())
                .targetType(deployment.getTargetType())
                .endpoint(deployment.getEndpoint())
                .estimatedTime(estimatedTime)
                .errorMessage(deployment.getErrorMessage())
                .createdAt(deployment.getDeployedAt())
                .completedAt(deployment.getCompletedAt())
                .build();
    }
}
