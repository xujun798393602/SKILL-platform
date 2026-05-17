package com.skill.platform.core.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.core.model.dto.ValidationResultDTO;
import com.skill.platform.core.service.validation.SkillValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for SKILL validation endpoints.
 * <p>
 * Exposes endpoints to trigger validation and retrieve the current
 * validation status of a SKILL.
 */
@RestController
@RequestMapping("/api/v1/skills")
@Slf4j
public class SkillValidationController {

    private final SkillValidationService skillValidationService;

    public SkillValidationController(SkillValidationService skillValidationService) {
        this.skillValidationService = skillValidationService;
    }

    /**
     * Trigger validation for a SKILL.
     * <p>
     * Runs all six validation dimensions (format, naming, content, version,
     * size, security) and updates the SKILL status to "validated" or "rejected".
     *
     * @param skillId the ID of the SKILL to validate
     * @return the validation result with per-dimension check details
     */
    @PostMapping("/{skillId}/validate")
    public ApiResponse<ValidationResultDTO> validateSkill(@PathVariable UUID skillId) {
        log.info("Validation requested for skill: {}", skillId);
        ValidationResultDTO result = skillValidationService.validateSkill(skillId);
        return ApiResponse.success(result);
    }

    /**
     * Get the current validation status of a SKILL.
     *
     * @param skillId the ID of the SKILL
     * @return the current validation status
     */
    @GetMapping("/{skillId}/validation")
    public ApiResponse<ValidationResultDTO> getValidationStatus(@PathVariable UUID skillId) {
        log.info("Validation status requested for skill: {}", skillId);
        ValidationResultDTO result = skillValidationService.getValidationStatus(skillId);
        return ApiResponse.success(result);
    }
}
