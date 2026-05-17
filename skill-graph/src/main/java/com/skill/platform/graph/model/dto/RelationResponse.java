package com.skill.platform.graph.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for SKILL relation data (F016 - SKILL图谱服务).
 * <p>
 * Supports both flat relation responses and tree-structured graph traversal results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationResponse {

    /**
     * The relation record ID.
     */
    private UUID id;

    /**
     * The source SKILL ID.
     */
    private UUID sourceSkillId;

    /**
     * The source SKILL name.
     */
    private String sourceSkillName;

    /**
     * The target SKILL ID.
     */
    private UUID targetSkillId;

    /**
     * The target SKILL name.
     */
    private String targetSkillName;

    /**
     * The relation type.
     */
    private String relationType;

    /**
     * Human-readable label.
     */
    private String label;

    /**
     * Creation timestamp.
     */
    private Instant createdAt;

    /**
     * Related skills (for tree-structured traversal responses).
     */
    private List<RelationResponse> children;
}
