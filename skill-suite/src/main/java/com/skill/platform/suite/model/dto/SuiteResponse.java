package com.skill.platform.suite.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO representing a SKILL suite with its associated skills.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuiteResponse {

    private UUID id;
    private String name;
    private String description;
    private String category;
    private String visibility;
    private String status;
    private String ownerName;
    private Instant createdAt;
    private Instant updatedAt;
    private List<SuiteSkillItem> skills;

    /**
     * A single skill entry within a suite response.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuiteSkillItem {

        private UUID skillId;
        private String skillName;
        private String skillType;
        private Integer sortOrder;
    }
}
