package com.skill.platform.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response returned after a successful SKILL upload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillUploadResponse {

    private UUID skillId;
    private String name;
    private String version;
    private String status;
    private String filePath;
}
