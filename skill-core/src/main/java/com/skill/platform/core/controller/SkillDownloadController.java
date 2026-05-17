package com.skill.platform.core.controller;

import com.skill.platform.core.model.dto.SkillDownloadResponse;
import com.skill.platform.core.service.download.SkillDownloadService;
import com.skill.platform.common.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/skills")
@Slf4j
public class SkillDownloadController {

    private final SkillDownloadService skillDownloadService;

    public SkillDownloadController(SkillDownloadService skillDownloadService) {
        this.skillDownloadService = skillDownloadService;
    }

    @GetMapping("/{skillId}/download")
    public ResponseEntity<Resource> downloadSkill(
            @PathVariable UUID skillId,
            @RequestParam(value = "version", required = false) String version) {

        UUID userId = UUID.fromString(UserContext.getUserId());
        SkillDownloadResponse response = skillDownloadService.downloadSkill(skillId, userId, version);

        InputStreamResource resource = new InputStreamResource(response.getInputStream());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + response.getFileName() + "\"")
            .contentType(MediaType.parseMediaType(response.getContentType()))
            .body(resource);
    }

    @PostMapping("/batch-download")
    public ResponseEntity<List<Resource>> batchDownload(
            @RequestBody List<UUID> skillIds) {

        UUID userId = UUID.fromString(UserContext.getUserId());
        List<SkillDownloadResponse> responses = skillDownloadService.batchDownload(skillIds, userId);

        // Note: For true batch download, you'd typically create a ZIP file
        // This is a simplified version returning the first file
        if (responses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        SkillDownloadResponse first = responses.get(0);
        InputStreamResource resource = new InputStreamResource(first.getInputStream());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + first.getFileName() + "\"")
            .contentType(MediaType.parseMediaType(first.getContentType()))
            .body(resource);
    }
}
