package com.skill.platform.core.repository;

import com.skill.platform.core.model.DownloadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DownloadLogRepository extends JpaRepository<DownloadLog, UUID> {

    /**
     * Check whether a user has downloaded a specific SKILL.
     *
     * @param skillId the SKILL ID
     * @param userId  the user ID
     * @return true if a download log record exists for the given skill and user
     */
    boolean existsBySkillIdAndUserId(UUID skillId, UUID userId);
}
