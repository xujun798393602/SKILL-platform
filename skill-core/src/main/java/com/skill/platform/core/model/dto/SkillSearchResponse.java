package com.skill.platform.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response item representing a single SKILL search result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillSearchResponse {

    private String id;
    private String name;
    private String description;
    private String skillType;
    private String category;
    private List<String> tags;
    private String ownerName;
    private Integer downloadCount;
    private Double avgRating;
}
