package com.skill.platform.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response returned after a batch SKILL upload, summarising success/failure counts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillBatchUploadResponse {

    private Integer total;
    private Integer success;
    private Integer failed;
    private List<String> errors;
}
