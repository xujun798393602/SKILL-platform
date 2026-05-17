package com.skill.platform.core.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SkillDownloadedEvent {
    private UUID skillId;
    private UUID userId;
}
