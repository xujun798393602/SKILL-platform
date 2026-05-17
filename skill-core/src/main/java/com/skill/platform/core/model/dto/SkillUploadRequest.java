package com.skill.platform.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request payload for uploading a single SKILL file.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillUploadRequest {

    private String name;
    private String description;
    private String skillType;
    private String category;
    private List<String> tags;
}
