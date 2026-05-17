package com.skill.platform.core.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.core.model.SkillVersion;
import com.skill.platform.core.model.dto.VersionTagRequest;
import com.skill.platform.core.service.version.SkillVersionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for SKILL version management endpoints.
 */
@RestController
@RequestMapping("/api/v1/skills/{skillId}/versions")
@Slf4j
public class SkillVersionController {

    private final SkillVersionService skillVersionService;

    public SkillVersionController(SkillVersionService skillVersionService) {
        this.skillVersionService = skillVersionService;
    }

    /**
     * List versions for a skill, paginated and sorted by createdAt DESC.
     *
     * @param skillId  the skill ID
     * @param pageable pagination parameters (default: page 0, size 20)
     * @return paginated list of versions
     */
    @GetMapping
    public ApiResponse<Page<SkillVersion>> listVersions(
            @PathVariable UUID skillId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(skillVersionService.listVersions(skillId, pageable));
    }

    /**
     * Get a specific version of a skill.
     *
     * @param skillId the skill ID
     * @param version the version string
     * @return the version details
     */
    @GetMapping("/{version}")
    public ApiResponse<SkillVersion> getVersion(
            @PathVariable UUID skillId,
            @PathVariable String version) {
        return ApiResponse.success(skillVersionService.getVersion(skillId, version));
    }

    /**
     * Rollback to a specific version.
     * <p>
     * Deactivates the current active version, activates the target version,
     * and updates the parent Skill record accordingly.
     *
     * @param skillId the skill ID
     * @param version the target version to activate
     * @return the newly activated version
     */
    @PostMapping("/{version}/rollback")
    public ApiResponse<SkillVersion> rollback(
            @PathVariable UUID skillId,
            @PathVariable String version) {
        return ApiResponse.success(skillVersionService.rollback(skillId, version));
    }

    /**
     * Set or update the tag for a specific version.
     *
     * @param skillId the skill ID
     * @param version the version string
     * @param request the tag request body
     * @return the updated version
     */
    @PutMapping("/{version}/tag")
    public ApiResponse<SkillVersion> setTag(
            @PathVariable UUID skillId,
            @PathVariable String version,
            @Valid @RequestBody VersionTagRequest request) {
        return ApiResponse.success(skillVersionService.setTag(skillId, version, request.getTag()));
    }
}
