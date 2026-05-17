package com.skill.platform.core.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.core.model.dto.SkillBatchUploadResponse;
import com.skill.platform.core.model.dto.SkillUploadRequest;
import com.skill.platform.core.model.dto.SkillUploadResponse;
import com.skill.platform.core.service.upload.SkillUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for SKILL file upload endpoints.
 */
@RestController
@RequestMapping("/api/v1/skills")
@Slf4j
public class SkillUploadController {

    private final SkillUploadService skillUploadService;

    public SkillUploadController(SkillUploadService skillUploadService) {
        this.skillUploadService = skillUploadService;
    }

    /**
     * Upload a single SKILL file with optional metadata.
     *
     * @param file        the SKILL file to upload
     * @param name        optional name override
     * @param description optional description
     * @param skillType   optional skill type (defaults to "personal")
     * @param category    optional category
     * @param tags        optional list of tag names
     * @return the created SKILL details
     */
    @PostMapping("/upload")
    public ApiResponse<SkillUploadResponse> uploadSkill(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "skillType", required = false) String skillType,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) List<String> tags) {

        UUID userId = UUID.fromString(UserContext.getUserId());
        SkillUploadRequest metadata = SkillUploadRequest.builder()
            .name(name)
            .description(description)
            .skillType(skillType)
            .category(category)
            .tags(tags)
            .build();

        return ApiResponse.success(skillUploadService.uploadSkill(file, userId, metadata));
    }

    /**
     * Upload multiple SKILL files in a single request.
     *
     * @param files the list of SKILL files to upload
     * @return batch upload summary with success/failure counts
     */
    @PostMapping("/batch-upload")
    public ApiResponse<SkillBatchUploadResponse> batchUpload(
            @RequestParam("files") List<MultipartFile> files) {
        UUID userId = UUID.fromString(UserContext.getUserId());
        return ApiResponse.success(skillUploadService.batchUpload(files, userId));
    }
}
