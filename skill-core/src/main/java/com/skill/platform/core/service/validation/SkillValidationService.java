package com.skill.platform.core.service.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.service.FileStorageService;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.model.dto.ValidationResultDTO;
import com.skill.platform.core.model.dto.ValidationResultDTO.ValidationCheck;
import com.skill.platform.core.repository.SkillRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for validating uploaded SKILL files across six dimensions:
 * format, naming, content, version, size, and security.
 * <p>
 * After validation completes, the Skill entity status is updated to either
 * "validated" or "rejected".
 */
@Service
@Slf4j
public class SkillValidationService {

    private final SkillRepository skillRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024; // 100MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".json", ".skill", ".zip");
    private static final Pattern NAMING_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_.\\-]{0,199}$");
    private static final Pattern SEMVER_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");
    private static final List<Pattern> SUSPICIOUS_PATTERNS = Arrays.asList(
            Pattern.compile("<script[^>]*>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
            Pattern.compile("eval\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("exec\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("System\\.(exit|exec)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Runtime\\.getRuntime", Pattern.CASE_INSENSITIVE),
            Pattern.compile("ProcessBuilder", Pattern.CASE_INSENSITIVE)
    );

    public SkillValidationService(SkillRepository skillRepository,
                                   FileStorageService fileStorageService,
                                   ObjectMapper objectMapper) {
        this.skillRepository = skillRepository;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
    }

    /**
     * Run all six validation dimensions against the identified SKILL and
     * update its status accordingly.
     *
     * @param skillId the ID of the Skill to validate
     * @return the validation result with per-dimension check details
     */
    @Transactional
    public ValidationResultDTO validateSkill(UUID skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new BusinessException("SYSTEM001", "Skill not found: " + skillId, 404));

        // Only skills in "draft" status can be validated
        if (!"draft".equals(skill.getStatus())) {
            throw new BusinessException("VALIDATION001",
                    "Skill is not in draft status, current status: " + skill.getStatus(), 400);
        }

        List<ValidationCheck> checks = new ArrayList<>();
        String fileContent = null;

        // Load file content for content/security checks when the file is a .json file
        String extension = getFileExtension(skill.getFilePath());
        if (".json".equals(extension) && skill.getFilePath() != null) {
            fileContent = loadFileContent(skill.getFilePath());
        }

        // Run all six dimensions
        checks.add(checkFormat(skill));
        checks.add(checkNaming(skill));
        checks.add(checkContent(skill, fileContent));
        checks.add(checkVersion(skill));
        checks.add(checkSize(skill));
        checks.add(checkSecurity(skill, fileContent));

        // Determine overall status
        boolean allPassed = checks.stream().allMatch(ValidationCheck::isPassed);
        String status = allPassed ? "validated" : "rejected";

        // Update skill status
        skill.setStatus(status);
        skillRepository.save(skill);

        log.info("Skill validation completed: skillId={}, status={}, checks passed={}/{}",
                skillId, status,
                checks.stream().filter(ValidationCheck::isPassed).count(),
                checks.size());

        return ValidationResultDTO.builder()
                .skillId(skillId)
                .status(status)
                .checks(checks)
                .build();
    }

    /**
     * Retrieve the current validation status of a Skill.
     *
     * @param skillId the ID of the Skill
     * @return the validation result reflecting the current status
     */
    @Transactional(readOnly = true)
    public ValidationResultDTO getValidationStatus(UUID skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new BusinessException("SYSTEM001", "Skill not found: " + skillId, 404));

        String status = skill.getStatus();

        // If the skill has not been validated yet, return a pending result
        if ("draft".equals(status)) {
            return ValidationResultDTO.builder()
                    .skillId(skillId)
                    .status("pending")
                    .checks(List.of())
                    .build();
        }

        // For validated/rejected skills, return the status without re-running checks
        return ValidationResultDTO.builder()
                .skillId(skillId)
                .status(status)
                .checks(List.of())
                .build();
    }

    // ---- Validation dimension methods ----

    /**
     * Dimension 1: Format check - file extension must be .json, .skill, or .zip.
     */
    private ValidationCheck checkFormat(Skill skill) {
        String filePath = skill.getFilePath();
        if (filePath == null || filePath.isBlank()) {
            return ValidationCheck.builder()
                    .dimension("format")
                    .passed(false)
                    .message("File path is missing")
                    .build();
        }

        String extension = getFileExtension(filePath);
        boolean passed = ALLOWED_EXTENSIONS.contains(extension);
        return ValidationCheck.builder()
                .dimension("format")
                .passed(passed)
                .message(passed ? "File format is valid: " + extension : "Unsupported file format: " + extension)
                .build();
    }

    /**
     * Dimension 2: Naming check - name must start with alphanumeric, only safe characters, 1-200 chars.
     */
    private ValidationCheck checkNaming(Skill skill) {
        String name = skill.getName();
        if (name == null || name.isBlank()) {
            return ValidationCheck.builder()
                    .dimension("naming")
                    .passed(false)
                    .message("Skill name is missing or empty")
                    .build();
        }

        boolean passed = NAMING_PATTERN.matcher(name).matches();
        return ValidationCheck.builder()
                .dimension("naming")
                .passed(passed)
                .message(passed ? "Skill name is valid" : "Skill name must start with an alphanumeric character and contain only letters, digits, underscore, dot, or dash (1-200 chars)")
                .build();
    }

    /**
     * Dimension 3: Content check - for .json files, validate JSON syntax.
     */
    private ValidationCheck checkContent(Skill skill, String fileContent) {
        String extension = getFileExtension(skill.getFilePath());

        // Content check only applies to .json files
        if (!".json".equals(extension)) {
            return ValidationCheck.builder()
                    .dimension("content")
                    .passed(true)
                    .message("Content check skipped for non-JSON file")
                    .build();
        }

        if (fileContent == null || fileContent.isBlank()) {
            return ValidationCheck.builder()
                    .dimension("content")
                    .passed(false)
                    .message("File content is empty or could not be read")
                    .build();
        }

        try {
            objectMapper.readTree(fileContent);
            return ValidationCheck.builder()
                    .dimension("content")
                    .passed(true)
                    .message("JSON syntax is valid")
                    .build();
        } catch (Exception e) {
            return ValidationCheck.builder()
                    .dimension("content")
                    .passed(false)
                    .message("Invalid JSON syntax: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Dimension 4: Version check - version must follow semver format (x.y.z).
     */
    private ValidationCheck checkVersion(Skill skill) {
        String version = skill.getVersion();
        if (version == null || version.isBlank()) {
            return ValidationCheck.builder()
                    .dimension("version")
                    .passed(false)
                    .message("Version is missing")
                    .build();
        }

        boolean passed = SEMVER_PATTERN.matcher(version).matches();
        return ValidationCheck.builder()
                .dimension("version")
                .passed(passed)
                .message(passed ? "Version format is valid: " + version : "Version must follow semver format (x.y.z), got: " + version)
                .build();
    }

    /**
     * Dimension 5: Size check - file size must not exceed 100MB.
     */
    private ValidationCheck checkSize(Skill skill) {
        Long fileSize = skill.getFileSize();
        if (fileSize == null) {
            return ValidationCheck.builder()
                    .dimension("size")
                    .passed(false)
                    .message("File size information is missing")
                    .build();
        }

        boolean passed = fileSize > 0 && fileSize <= MAX_FILE_SIZE;
        return ValidationCheck.builder()
                .dimension("size")
                .passed(passed)
                .message(passed ? "File size is within limit: " + fileSize + " bytes" : "File size exceeds 100MB limit: " + fileSize + " bytes")
                .build();
    }

    /**
     * Dimension 6: Security check - no suspicious patterns in file content (script injection, etc.).
     */
    private ValidationCheck checkSecurity(Skill skill, String fileContent) {
        String extension = getFileExtension(skill.getFilePath());

        // Security check only applies to text-based files (.json, .skill)
        if (!".json".equals(extension) && !".skill".equals(extension)) {
            return ValidationCheck.builder()
                    .dimension("security")
                    .passed(true)
                    .message("Security check skipped for binary file")
                    .build();
        }

        if (fileContent == null || fileContent.isBlank()) {
            return ValidationCheck.builder()
                    .dimension("security")
                    .passed(true)
                    .message("No content to scan for security issues")
                    .build();
        }

        for (Pattern pattern : SUSPICIOUS_PATTERNS) {
            if (pattern.matcher(fileContent).find()) {
                return ValidationCheck.builder()
                        .dimension("security")
                        .passed(false)
                        .message("Suspicious pattern detected: " + pattern.pattern())
                        .build();
            }
        }

        return ValidationCheck.builder()
                .dimension("security")
                .passed(true)
                .message("No security issues detected")
                .build();
    }

    // ---- Helper methods ----

    /**
     * Extract the file extension (including the leading dot) from a file path.
     */
    private String getFileExtension(String filePath) {
        if (filePath == null) {
            return "";
        }
        int lastDot = filePath.lastIndexOf('.');
        return lastDot >= 0 ? filePath.substring(lastDot) : "";
    }

    /**
     * Load the full text content of a file from MinIO storage.
     */
    private String loadFileContent(String filePath) {
        try (InputStream is = fileStorageService.downloadFile(filePath)) {
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.warn("Failed to load file content for validation: {}", filePath, e);
            return null;
        }
    }
}
