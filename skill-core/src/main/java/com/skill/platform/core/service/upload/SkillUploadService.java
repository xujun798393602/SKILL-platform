package com.skill.platform.core.service.upload;

import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.service.FileStorageService;
import com.skill.platform.core.event.SkillUploadedEvent;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.model.SkillFile;
import com.skill.platform.core.model.SkillTag;
import com.skill.platform.core.model.SkillVersion;
import com.skill.platform.core.model.Tag;
import com.skill.platform.auth.model.User;
import com.skill.platform.core.model.dto.SkillBatchUploadResponse;
import com.skill.platform.core.model.dto.SkillUploadRequest;
import com.skill.platform.core.model.dto.SkillUploadResponse;
import com.skill.platform.core.repository.SkillFileRepository;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.core.repository.SkillTagRepository;
import com.skill.platform.core.repository.SkillVersionRepository;
import com.skill.platform.core.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * Business logic for SKILL file upload operations.
 * <p>
 * Handles single and batch uploads, including file validation, checksum
 * computation, MinIO storage, database persistence, tag management, and
 * event publishing.
 */
@Service
@Slf4j
public class SkillUploadService {

    private final SkillRepository skillRepository;
    private final SkillVersionRepository skillVersionRepository;
    private final SkillFileRepository skillFileRepository;
    private final TagRepository tagRepository;
    private final SkillTagRepository skillTagRepository;
    private final FileStorageService fileStorageService;
    private final ApplicationEventPublisher eventPublisher;

    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024; // 100MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".json", ".skill", ".zip");

    public SkillUploadService(SkillRepository skillRepository,
                               SkillVersionRepository skillVersionRepository,
                               SkillFileRepository skillFileRepository,
                               TagRepository tagRepository,
                               SkillTagRepository skillTagRepository,
                               FileStorageService fileStorageService,
                               ApplicationEventPublisher eventPublisher) {
        this.skillRepository = skillRepository;
        this.skillVersionRepository = skillVersionRepository;
        this.skillFileRepository = skillFileRepository;
        this.tagRepository = tagRepository;
        this.skillTagRepository = skillTagRepository;
        this.fileStorageService = fileStorageService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Upload a single SKILL file with associated metadata.
     *
     * @param file     the uploaded multipart file
     * @param userId   the ID of the uploading user
     * @param metadata optional metadata for the SKILL
     * @return upload response with the created SKILL details
     */
    @Transactional
    public SkillUploadResponse uploadSkill(MultipartFile file, UUID userId, SkillUploadRequest metadata) {
        // Pre-validate file extension
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("UPLOAD001", "Unsupported file format: " + extension, 400);
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("UPLOAD002", "File size exceeds 100MB limit", 400);
        }

        // Validate filename
        if (!isValidFilename(file.getOriginalFilename())) {
            throw new BusinessException("UPLOAD003", "Invalid filename format", 400);
        }

        // Compute checksum
        String checksum = computeChecksum(file);

        // Create Skill record
        Skill skill = Skill.builder()
            .name(metadata.getName() != null ? metadata.getName() : file.getOriginalFilename())
            .description(metadata.getDescription())
            .skillType(metadata.getSkillType() != null ? metadata.getSkillType() : "personal")
            .category(metadata.getCategory())
            .status("draft")
            .owner(User.builder().id(userId).build())
            .version("1.0.0")
            .fileSize(file.getSize())
            .checksum(checksum)
            .downloadCount(0)
            .avgRating(BigDecimal.ZERO)
            .ratingCount(0)
            .build();

        skill = skillRepository.save(skill);

        // Upload file to MinIO
        String objectPath = fileStorageService.buildObjectPath(skill.getId(), "1.0.0", file.getOriginalFilename());
        try {
            fileStorageService.uploadFile(file.getInputStream(), objectPath, file.getContentType());
        } catch (IOException e) {
            throw new BusinessException("UPLOAD004", "Failed to upload file", 500);
        }

        // Update skill with file path
        skill.setFilePath(objectPath);
        skill = skillRepository.save(skill);

        // Create version record
        SkillVersion version = SkillVersion.builder()
            .skill(skill)
            .version("1.0.0")
            .filePath(objectPath)
            .fileSize(file.getSize())
            .checksum(checksum)
            .isActive(true)
            .build();
        skillVersionRepository.save(version);

        // Create file record
        SkillFile skillFile = SkillFile.builder()
            .skill(skill)
            .fileName(file.getOriginalFilename())
            .filePath(objectPath)
            .fileType(extension)
            .fileSize(file.getSize())
            .checksum(checksum)
            .build();
        skillFileRepository.save(skillFile);

        // Process tags
        if (metadata.getTags() != null && !metadata.getTags().isEmpty()) {
            processTags(skill, metadata.getTags());
        }

        log.info("Skill uploaded: {} by user {}", skill.getId(), userId);

        // Publish event
        eventPublisher.publishEvent(new SkillUploadedEvent(skill.getId(), userId));

        return SkillUploadResponse.builder()
            .skillId(skill.getId())
            .name(skill.getName())
            .version("1.0.0")
            .status("draft")
            .filePath(objectPath)
            .build();
    }

    /**
     * Upload multiple SKILL files in a single request.
     * Each file is processed independently; failures are captured per-file.
     *
     * @param files  the list of uploaded files
     * @param userId the ID of the uploading user
     * @return batch upload response with success/failure counts
     */
    @Transactional
    public SkillBatchUploadResponse batchUpload(List<MultipartFile> files, UUID userId) {
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                SkillUploadRequest metadata = SkillUploadRequest.builder()
                    .name(file.getOriginalFilename())
                    .skillType("personal")
                    .build();
                uploadSkill(file, userId, metadata);
                successCount++;
            } catch (Exception e) {
                failCount++;
                errors.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        return SkillBatchUploadResponse.builder()
            .total(files.size())
            .success(successCount)
            .failed(failCount)
            .errors(errors)
            .build();
    }

    /**
     * Associate tags with a SKILL, creating new Tag records as needed.
     */
    private void processTags(Skill skill, List<String> tagNames) {
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));

            SkillTag skillTag = SkillTag.builder()
                .skill(skill)
                .tag(tag)
                .build();
            skillTagRepository.save(skillTag);
        }
    }

    /**
     * Extract the file extension (including the leading dot) from a filename.
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot >= 0 ? filename.substring(lastDot) : "";
    }

    /**
     * Validate that the filename starts with an alphanumeric character and
     * contains only safe characters (letters, digits, underscore, dot, dash).
     */
    private boolean isValidFilename(String filename) {
        return filename != null && filename.matches("^[a-zA-Z0-9][a-zA-Z0-9_.-]{0,254}$");
    }

    /**
     * Compute the SHA-256 checksum of the uploaded file contents.
     */
    private String computeChecksum(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new BusinessException("UPLOAD005", "Failed to compute checksum", 500);
        }
    }
}
