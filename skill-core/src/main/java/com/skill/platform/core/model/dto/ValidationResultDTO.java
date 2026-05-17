package com.skill.platform.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO representing the result of a SKILL validation process.
 * Contains the overall status and individual check results for each validation dimension.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResultDTO {

    private UUID skillId;
    private String status;
    private List<ValidationCheck> checks;

    /**
     * Represents the result of a single validation dimension check.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationCheck {

        /** The validation dimension (e.g. "format", "naming", "content", "version", "size", "security"). */
        private String dimension;

        /** Whether this check passed. */
        private boolean passed;

        /** Human-readable message describing the check outcome. */
        private String message;
    }
}
