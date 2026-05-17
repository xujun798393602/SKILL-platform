package com.skill.platform.core.service.download;

import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.service.FileStorageService;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.core.event.SkillDownloadedEvent;
import com.skill.platform.core.model.DownloadLog;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.model.SkillVersion;
import com.skill.platform.core.model.dto.SkillDownloadResponse;
import com.skill.platform.core.repository.DownloadLogRepository;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.core.repository.SkillVersionRepository;
import com.skill.platform.auth.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SkillDownloadService {

    private final SkillRepository skillRepository;
    private final SkillVersionRepository skillVersionRepository;
    private final DownloadLogRepository downloadLogRepository;
    private final FileStorageService fileStorageService;
    private final ApplicationEventPublisher eventPublisher;

    private static final int MAX_BATCH_DOWNLOAD = 50;

    public SkillDownloadService(SkillRepository skillRepository,
                                 SkillVersionRepository skillVersionRepository,
                                 DownloadLogRepository downloadLogRepository,
                                 FileStorageService fileStorageService,
                                 ApplicationEventPublisher eventPublisher) {
        this.skillRepository = skillRepository;
        this.skillVersionRepository = skillVersionRepository;
        this.downloadLogRepository = downloadLogRepository;
        this.fileStorageService = fileStorageService;
        this.eventPublisher = eventPublisher;
    }

    public SkillDownloadResponse downloadSkill(UUID skillId, UUID userId, String version) {
        // Check skill exists
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new BusinessException("DOWNLOAD001", "Skill not found", 404));

        // Check download permission (private skills require ownership)
        if (skill.getSkillType().equals("private") && !skill.getOwner().getId().equals(userId)) {
            throw new BusinessException("DOWNLOAD002", "No permission to download private skill", 403);
        }

        // Get file path
        String filePath;
        if (version != null) {
            SkillVersion skillVersion = skillVersionRepository.findBySkillIdAndVersion(skillId, version)
                .orElseThrow(() -> new BusinessException("DOWNLOAD003", "Version not found", 404));
            filePath = skillVersion.getFilePath();
        } else {
            filePath = skill.getFilePath();
        }

        // Download from MinIO
        InputStream inputStream = fileStorageService.downloadFile(filePath);

        // Log download
        DownloadLog downloadLog = DownloadLog.builder()
            .skill(skill)
            .user(User.builder().id(userId).build())
            .version(version != null ? version : skill.getVersion())
            .ipAddress(UserContext.getIpAddress())
            .userAgent(UserContext.getUserAgent())
            .build();
        downloadLogRepository.save(downloadLog);

        // Update download count
        skill.setDownloadCount(skill.getDownloadCount() + 1);
        skillRepository.save(skill);

        // Publish event
        eventPublisher.publishEvent(new SkillDownloadedEvent(skillId, userId));

        log.info("Skill downloaded: {} by user {}", skillId, userId);

        return SkillDownloadResponse.builder()
            .inputStream(inputStream)
            .fileName(skill.getName() + getFileExtension(filePath))
            .contentType("application/octet-stream")
            .build();
    }

    public List<SkillDownloadResponse> batchDownload(List<UUID> skillIds, UUID userId) {
        if (skillIds.size() > MAX_BATCH_DOWNLOAD) {
            throw new BusinessException("DOWNLOAD004", "Batch download limit exceeded (max " + MAX_BATCH_DOWNLOAD + ")", 400);
        }

        List<SkillDownloadResponse> responses = new ArrayList<>();
        for (UUID skillId : skillIds) {
            try {
                responses.add(downloadSkill(skillId, userId, null));
            } catch (Exception e) {
                log.warn("Failed to download skill {}: {}", skillId, e.getMessage());
            }
        }

        return responses;
    }

    private String getFileExtension(String filePath) {
        if (filePath == null) return "";
        int lastDot = filePath.lastIndexOf('.');
        return lastDot >= 0 ? filePath.substring(lastDot) : "";
    }
}
