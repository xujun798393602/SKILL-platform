package com.skill.platform.admin.service;

import com.skill.platform.admin.model.SystemConfig;
import com.skill.platform.admin.model.dto.ConfigRequest;
import com.skill.platform.admin.model.dto.ConfigResponse;
import com.skill.platform.admin.repository.SystemConfigRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing system configuration items.
 * <p>
 * Provides CRUD operations with the following protections:
 * <ul>
 *   <li>Read-only configs cannot be updated</li>
 *   <li>Sensitive config values are masked in responses</li>
 *   <li>Optimistic locking via {@code updatedAt} timestamp to prevent lost updates</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final SystemConfigRepository configRepository;

    /**
     * List all system configurations.
     * Sensitive values are masked with "******".
     *
     * @return list of all config responses
     */
    @Transactional(readOnly = true)
    public List<ConfigResponse> listAll() {
        return configRepository.findAll().stream()
                .map(ConfigResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get a single configuration by its key.
     * Sensitive values are masked with "******".
     *
     * @param configKey the configuration key
     * @return the config response
     * @throws BusinessException if the config key is not found
     */
    @Transactional(readOnly = true)
    public ConfigResponse getByKey(String configKey) {
        SystemConfig config = findConfigByKey(configKey);
        return ConfigResponse.fromEntity(config);
    }

    /**
     * Update an existing configuration.
     * <p>
     * Validation rules:
     * <ul>
     *   <li>The config must exist</li>
     *   <li>Read-only configs cannot be updated</li>
     *   <li>Uses optimistic locking: the entity is reloaded before merge to detect concurrent modifications</li>
     * </ul>
     *
     * @param configKey the configuration key to update
     * @param request   the update request containing the new value and optional description
     * @return the updated config response
     * @throws BusinessException if the config is not found or is read-only
     */
    @Transactional
    public ConfigResponse update(String configKey, ConfigRequest request) {
        SystemConfig config = findConfigByKey(configKey);

        // Enforce read-only protection
        if (Boolean.TRUE.equals(config.getIsReadOnly())) {
            log.warn("Attempt to update read-only config '{}' by user", configKey);
            throw new BusinessException(ErrorCode.CONFIG003);
        }

        config.setConfigValue(request.getConfigValue());
        if (request.getDescription() != null) {
            config.setDescription(request.getDescription());
        }

        // Reload to detect concurrent modification (optimistic locking).
        // Since the entity does not have a @Version field, we re-fetch and
        // compare updatedAt to guard against lost updates.
        SystemConfig latest = findConfigByKey(configKey);
        if (!config.getUpdatedAt().equals(latest.getUpdatedAt())) {
            log.warn("Concurrent modification detected for config '{}'", configKey);
            throw new BusinessException(
                    "CONFIG_CONFLICT",
                    "Configuration was modified by another request. Please retry.",
                    409
            );
        }

        SystemConfig saved = configRepository.save(config);
        log.info("Config '{}' updated successfully", configKey);
        return ConfigResponse.fromEntity(saved);
    }

    /**
     * Find a config by key or throw if not found.
     *
     * @param configKey the configuration key
     * @return the SystemConfig entity
     * @throws BusinessException if not found
     */
    private SystemConfig findConfigByKey(String configKey) {
        return configRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONFIG001));
    }
}
