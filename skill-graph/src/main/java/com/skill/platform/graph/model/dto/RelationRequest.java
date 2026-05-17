package com.skill.platform.graph.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a SKILL relation (F016 - SKILL图谱服务).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationRequest {

    /**
     * The ID of the target SKILL in the relation.
     */
    @NotNull(message = "Target skill ID is required")
    private UUID targetSkillId;

    /**
     * The type of relation (e.g. "depends_on", "extends", "related_to").
     */
    @NotBlank(message = "Relation type is required")
    @Size(max = 50, message = "Relation type must not exceed 50 characters")
    private String relationType;

    /**
     * Optional human-readable label for the relation.
     */
    @Size(max = 200, message = "Label must not exceed 200 characters")
    private String label;
}
