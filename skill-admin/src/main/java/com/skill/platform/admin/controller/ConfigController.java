package com.skill.platform.admin.controller;

import com.skill.platform.admin.model.dto.ConfigRequest;
import com.skill.platform.admin.model.dto.ConfigResponse;
import com.skill.platform.admin.service.ConfigService;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.util.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for system configuration management.
 * <p>
 * All endpoints require the caller to have the {@code ADMIN} role.
 * Sensitive configuration values are automatically masked in responses.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    /**
     * List all system configurations.
     * <p>
     * Sensitive values are masked with "******".
     *
     * @return API response containing the list of all configurations
     */
    @GetMapping
    public ApiResponse<List<ConfigResponse>> listAll() {
        requireAdmin();
        List<ConfigResponse> configs = configService.listAll();
        return ApiResponse.success(configs);
    }

    /**
     * Get a configuration by its key.
     * <p>
     * Sensitive values are masked with "******".
     *
     * @param key the configuration key
     * @return API response containing the configuration
     */
    @GetMapping("/{key}")
    public ApiResponse<ConfigResponse> getByKey(@PathVariable("key") String key) {
        requireAdmin();
        ConfigResponse config = configService.getByKey(key);
        return ApiResponse.success(config);
    }

    /**
     * Update a configuration value.
     * <p>
     * Read-only configurations cannot be updated (HTTP 403).
     * Requires ADMIN role.
     *
     * @param key     the configuration key to update
     * @param request the update request body
     * @return API response containing the updated configuration
     */
    @PutMapping("/{key}")
    public ApiResponse<ConfigResponse> update(
            @PathVariable("key") String key,
            @Valid @RequestBody ConfigRequest request) {
        requireAdmin();
        ConfigResponse updated = configService.update(key, request);
        return ApiResponse.success("Configuration updated successfully", updated);
    }

    /**
     * Verify that the current user has the ADMIN role.
     *
     * @throws BusinessException if the user does not have the ADMIN role
     */
    private void requireAdmin() {
        if (!UserContext.hasRole("ADMIN")) {
            throw new BusinessException(ErrorCode.AUTH003);
        }
    }
}
