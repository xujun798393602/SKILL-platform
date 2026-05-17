package com.skill.platform.core.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Application event published when a new SKILL is uploaded.
 * Listeners can use this to trigger downstream processing such as
 * indexing, notification, or approval workflows.
 */
@Data
@AllArgsConstructor
public class SkillUploadedEvent {

    private UUID skillId;
    private UUID userId;
}
