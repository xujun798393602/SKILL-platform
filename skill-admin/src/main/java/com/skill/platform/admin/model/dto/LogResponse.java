package com.skill.platform.admin.model.dto;

import com.skill.platform.admin.model.OperationLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for operation log query responses.
 * Maps from {@link OperationLog} entity to a flat response structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogResponse {

    private UUID id;
    private String logType;
    private String action;
    private UUID userId;
    private String userName;
    private String resourceType;
    private UUID resourceId;
    private String resourceName;
    private String ipAddress;
    private String result;
    private String detail;
    private Instant createdAt;

    /**
     * Convert an {@link OperationLog} entity to a {@link LogResponse} DTO.
     *
     * @param log the operation log entity
     * @return the response DTO
     */
    public static LogResponse from(OperationLog log) {
        return LogResponse.builder()
                .id(log.getId())
                .logType(log.getLogType())
                .action(log.getAction())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .userName(log.getUserName())
                .resourceType(log.getResourceType())
                .resourceId(log.getResourceId())
                .resourceName(log.getResourceName())
                .ipAddress(log.getIpAddress())
                .result(log.getResult())
                .detail(log.getDetail())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
