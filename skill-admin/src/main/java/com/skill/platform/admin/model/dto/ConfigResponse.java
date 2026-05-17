package com.skill.platform.admin.model.dto;

import com.skill.platform.admin.model.SystemConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for system configuration items.
 * <p>
 * Sensitive values are masked with "******" before being returned to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigResponse {

    private UUID id;
    private String configKey;
    private String configValue;
    private String description;
    private Boolean isSensitive;
    private Boolean isReadOnly;
    private Instant createdAt;
    private Instant updatedAt;

    private static final String MASKED_VALUE = "******";

    /**
     * Convert a {@link SystemConfig} entity to a ConfigResponse.
     * Sensitive config values are masked.
     *
     * @param entity the SystemConfig entity
     * @return the ConfigResponse DTO
     */
    public static ConfigResponse fromEntity(SystemConfig entity) {
        String value = Boolean.TRUE.equals(entity.getIsSensitive())
                ? MASKED_VALUE
                : entity.getConfigValue();

        return ConfigResponse.builder()
                .id(entity.getId())
                .configKey(entity.getConfigKey())
                .configValue(value)
                .description(entity.getDescription())
                .isSensitive(entity.getIsSensitive())
                .isReadOnly(entity.getIsReadOnly())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
