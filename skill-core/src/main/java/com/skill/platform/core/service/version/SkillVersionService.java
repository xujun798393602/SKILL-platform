package com.skill.platform.core.service.version;

import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.model.SkillVersion;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.core.repository.SkillVersionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Business logic for SKILL version management operations.
 * <p>
 * Handles version listing, rollback, and tag management. Rollback uses
 * optimistic locking to prevent concurrent rollback conflicts.
 */
@Service
@Slf4j
public class SkillVersionService {

    private final SkillVersionRepository skillVersionRepository;
    private final SkillRepository skillRepository;

    public SkillVersionService(SkillVersionRepository skillVersionRepository,
                               SkillRepository skillRepository) {
        this.skillVersionRepository = skillVersionRepository;
        this.skillRepository = skillRepository;
    }

    /**
     * List versions for a skill, paginated and sorted by createdAt DESC.
     *
     * @param skillId  the skill ID
     * @param pageable pagination parameters
     * @return a page of SkillVersion records
     */
    @Transactional(readOnly = true)
    public Page<SkillVersion> listVersions(UUID skillId, Pageable pageable) {
        // Verify skill exists
        skillRepository.findById(skillId)
                .orElseThrow(() -> new BusinessException("VERSION001",
                        "Skill not found: " + skillId, 404));

        return skillVersionRepository.findBySkillIdOrderByCreatedAtDesc(skillId, pageable);
    }

    /**
     * Get a specific version of a skill.
     *
     * @param skillId the skill ID
     * @param version the version string
     * @return the matching SkillVersion
     */
    @Transactional(readOnly = true)
    public SkillVersion getVersion(UUID skillId, String version) {
        return skillVersionRepository.findBySkillIdAndVersion(skillId, version)
                .orElseThrow(() -> new BusinessException("VERSION001",
                        "Version not found: " + version + " for skill: " + skillId, 404));
    }

    /**
     * Rollback to a specific version.
     * <p>
     * Deactivates the current active version, activates the target version,
     * and updates the Skill record to reflect the new active version.
     * Uses optimistic locking to prevent concurrent rollback conflicts.
     *
     * @param skillId the skill ID
     * @param version the target version to activate
     * @return the newly activated SkillVersion
     */
    @Transactional
    public SkillVersion rollback(UUID skillId, String version) {
        // Verify skill exists
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new BusinessException("VERSION001",
                        "Skill not found: " + skillId, 404));

        // Find target version
        SkillVersion targetVersion = skillVersionRepository.findBySkillIdAndVersion(skillId, version)
                .orElseThrow(() -> new BusinessException("VERSION001",
                        "Version not found: " + version + " for skill: " + skillId, 404));

        try {
            // Deactivate current active version (if any)
            skillVersionRepository.findBySkillIdAndIsActiveTrue(skillId)
                    .ifPresent(active -> {
                        active.setIsActive(false);
                        skillVersionRepository.save(active);
                    });

            // Activate target version
            targetVersion.setIsActive(true);
            targetVersion = skillVersionRepository.save(targetVersion);

            // Update Skill record to match target version
            skill.setVersion(targetVersion.getVersion());
            skill.setFilePath(targetVersion.getFilePath());
            skill.setChecksum(targetVersion.getChecksum());
            skill.setFileSize(targetVersion.getFileSize());
            skillRepository.save(skill);

            log.info("Rolled back skill {} to version {}", skillId, version);
            return targetVersion;

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException("VERSION002",
                    "Rollback failed due to concurrent modification, please retry", 409);
        }
    }

    /**
     * Set or update the tag for a specific version.
     *
     * @param skillId the skill ID
     * @param version the version string
     * @param tag     the tag value (max 50 characters)
     * @return the updated SkillVersion
     */
    @Transactional
    public SkillVersion setTag(UUID skillId, String version, String tag) {
        SkillVersion skillVersion = skillVersionRepository.findBySkillIdAndVersion(skillId, version)
                .orElseThrow(() -> new BusinessException("VERSION001",
                        "Version not found: " + version + " for skill: " + skillId, 404));

        if (tag != null && tag.length() > 50) {
            throw new BusinessException("VERSION003",
                    "Tag must not exceed 50 characters", 400);
        }

        skillVersion.setTag(tag);
        skillVersion = skillVersionRepository.save(skillVersion);

        log.info("Set tag '{}' on skill {} version {}", tag, skillId, version);
        return skillVersion;
    }
}
